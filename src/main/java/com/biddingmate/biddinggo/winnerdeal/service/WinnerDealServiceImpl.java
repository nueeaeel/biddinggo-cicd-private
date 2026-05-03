package com.biddingmate.biddinggo.winnerdeal.service;

import com.biddingmate.biddinggo.auction.dto.RefundDto;
import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.auction.prediction.event.AuctionPriceReferenceSyncRequestedEvent;
import com.biddingmate.biddinggo.bid.dto.BidResponse;
import com.biddingmate.biddinggo.bid.mapper.BidMapper;
import com.biddingmate.biddinggo.bid.service.BidQueryService;
import com.biddingmate.biddinggo.bid.service.BidService;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.item.mapper.AuctionItemMapper;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.item.model.AuctionItemStatus;
import com.biddingmate.biddinggo.member.service.MemberService;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import com.biddingmate.biddinggo.notification.service.NotificationPublisher;
import com.biddingmate.biddinggo.point.model.PointHistory;
import com.biddingmate.biddinggo.point.model.PointHistoryType;
import com.biddingmate.biddinggo.point.service.PointService;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealShippingAddressRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealTrackingNumberRequest;
import com.biddingmate.biddinggo.winnerdeal.mapper.WinnerDealMapper;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDeal;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WinnerDealServiceImpl implements WinnerDealService {
    private static final DateTimeFormatter DEAL_NUMBER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int DEAL_NUMBER_MAX_RETRY_COUNT = 10;

    private final AuctionMapper auctionMapper;
    private final BidMapper bidMapper;
    private final BidService bidService;
    private final BidQueryService bidQueryService;
    private final WinnerDealMapper winnerDealMapper;
    private final WinnerDealQueryService winnerDealQueryService;
    private final AuctionItemMapper auctionItemMapper;
    private final MemberService memberService;
    private final PointService pointService;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public void processClosing(Auction auction) {
        // 최고 입찰자 1명 조회
        List<BidResponse> topBids = bidMapper.getBidsByAuctionId(
                new RowBounds(0, 1),
                auction.getId(),
                "DESC"
        );

        if (topBids != null && !topBids.isEmpty()) {
            // 낙찰
            BidResponse winnerBid = topBids.get(0);
            AuctionItem auctionItem = auctionItemMapper.findById(auction.getItemId());

            // 낙찰가 결정: 비크리 가격이 있으면 사용, 없으면 경매 시작가 사용
            Long finalPrice = (auction.getVickreyPrice() != null)
                    ? auction.getVickreyPrice()
                    : auction.getStartPrice();

            WinnerDeal winnerDeal = WinnerDeal.builder()
                    .auctionId(auction.getId())
                    .winnerId(winnerBid.getBidderId())
                    .sellerId(auction.getSellerId())
                    .dealNumber(generateDealNumber())
                    .winnerPrice(finalPrice)
                    .status(WinnerDealStatus.PAID)
                    .createdAt(LocalDateTime.now())
                    .build();

            winnerDealMapper.insert(winnerDeal);

            // Auction 테이블 상태 및 결과 업데이트
            auction.setStatus(AuctionStatus.ENDED);
            auction.setWinnerId(winnerBid.getBidderId());
            auction.setWinnerPrice(finalPrice);

            int updatedRows = auctionMapper.updateAuctionResult(auction);
            if (updatedRows != 1) {
                throw new CustomException(ErrorType.ITEM_NOT_AUCTIONABLE);
            }

            auctionItemMapper.updateStatus(
                    auction.getItemId(),
                    AuctionItemStatus.SOLD,
                    AuctionItemStatus.ON_AUCTION,
                    null
            );

            publishAuctionPriceReferenceSyncRequestedEvent(auction, auctionItem, finalPrice);

            log.info("낙찰 처리 성공 - Auction ID: {}, Winner: {}, Price: {}",
                    auction.getId(), winnerBid.getBidderId(), finalPrice);

            // 낙찰 알람(구매자)
            notificationPublisher.publishNotification(
                    winnerBid.getBidderId(),
                    NotificationType.WIN,
                    "축하합니다. 경매 #" + auction.getId() + " 낙찰이 확정되었습니다.",
                    "/auctions/" + auction.getId()
            );

            // 낙찰 알람(판매자)
            notificationPublisher.publishNotification(
                    auction.getSellerId(),
                    NotificationType.WIN,
                    "경매 #" + auction.getId() + " 낙찰이 확정되었습니다.",
                    "/auctions/" + auction.getId()
            );


            List<RefundDto> refunds =
                    bidQueryService.findRefundTargetsExcludingWinner(auction.getId(), winnerBid.getBidderId());

            for (RefundDto refund : refunds) {
                pointService.refundBid(refund.getBidderId(), refund.getAmount());
            }

        } else {
            // 유찰
            auction.setStatus(AuctionStatus.ENDED);

            int updatedRows = auctionMapper.updateAuctionResult(auction);
            if (updatedRows != 1) {
                throw new CustomException(ErrorType.ITEM_NOT_AUCTIONABLE);
            }

            auctionItemMapper.updateStatus(
                    auction.getItemId(),
                    AuctionItemStatus.UNSOLD,
                    AuctionItemStatus.ON_AUCTION,
                    null
            );
            log.info("유찰 처리 완료 - Auction ID: {}", auction.getId());

            // 유찰 알람
            notificationPublisher.publishNotification(
                    auction.getSellerId(),
                    NotificationType.AUCTION_UNSOLD,
                    "경매 #" + auction.getId() + "가 유찰되었습니다.",
                    "/auctions/" + auction.getId()
            );

        }
    }

    @Override
    @Transactional
    public void processBuyNow(Auction auction, Long buyerId, Long buyNowPrice, long additionalAmount) {
        // 기존 입찰금을 제외하고 부족한 금액만 추가 차감한다.
        memberService.deductPoint(buyerId, additionalAmount);

        // 즉시구매로 추가 차감된 금액을 입찰 포인트 히스토리로 남긴다.
        PointHistory pointHistory = PointHistory.builder()
                .memberId(buyerId)
                .type(PointHistoryType.BID)
                .amount(additionalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        int pointInsert = pointService.addPointHistory(pointHistory);
        if (pointInsert != 1) {
            throw new CustomException(ErrorType.POINT_HISTORY_SAVE_FAILED);
        }

        // 거래 완료 후 시세 반영 이벤트에 사용할 상품 정보를 조회한다.
        AuctionItem auctionItem = auctionItemMapper.findById(auction.getItemId());

        // 즉시구매 성사와 동시에 낙찰 거래를 생성한다.
        WinnerDeal winnerDeal = WinnerDeal.builder()
                .auctionId(auction.getId())
                .winnerId(buyerId)
                .sellerId(auction.getSellerId())
                .dealNumber(generateDealNumber())
                .winnerPrice(buyNowPrice)
                .status(WinnerDealStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();

        winnerDealMapper.insert(winnerDeal);

        // 경매를 종료하고 낙찰자와 최종 거래 금액을 확정한다.
        auction.setStatus(AuctionStatus.ENDED);
        auction.setWinnerId(buyerId);
        auction.setWinnerPrice(buyNowPrice);

        int updatedRows = auctionMapper.updateAuctionResult(auction);
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.ITEM_NOT_AUCTIONABLE);
        }

        // 경매 상품도 판매 완료 상태로 변경한다.
        auctionItemMapper.updateStatus(
                auction.getItemId(),
                AuctionItemStatus.SOLD,
                AuctionItemStatus.ON_AUCTION,
                null
        );

        // 즉시구매 완료 가격을 시세/예측 데이터에 반영할 수 있도록 이벤트를 발행한다.
        publishAuctionPriceReferenceSyncRequestedEvent(auction, auctionItem, buyNowPrice);

        // 낙찰자를 제외한 기존 입찰자들의 보증금성 입찰 금액을 환불한다.
        List<RefundDto> refunds =
                bidQueryService.findRefundTargetsExcludingWinner(auction.getId(), buyerId);

        for (RefundDto refund : refunds) {
            pointService.refundBid(refund.getBidderId(), refund.getAmount());
        }

        // 거래 완료 사실을 구매자와 판매자에게 각각 알린다.
        notificationPublisher.publishNotification(
                buyerId,
                NotificationType.WIN,
                "경매 #" + auction.getId() + "의 즉시구매가 완료되었습니다.",
                "/auctions/" + auction.getId()
        );

        notificationPublisher.publishNotification(
                auction.getSellerId(),
                NotificationType.WIN,
                "경매 #" + auction.getId() + "이 즉시구매로 종료되었습니다.",
                "/auctions/" + auction.getId()
        );
    }

    @Override
    @Transactional
    public void handleMemberDeactivationAfterWinning(Long memberId) {
        log.info("낙찰 회원 비활성화 처리 시작 - Member ID: {}", memberId);

        handleWinnerDeactivation(memberId);
        handleSellerDeactivation(memberId);
    }
/*
        for (WinnerDeal winnerDeal : winnerDeals) {
            if (isShippingInfoRegistered(winnerDeal)) {
                log.info("낙찰 회원 비활성화 거래 유지 - WinnerDeal ID: {}, Auction ID: {}",
                        winnerDeal.getId(), winnerDeal.getAuctionId());
                continue;
            }
            if(winnerDeal.getStatus()==WinnerDealStatus.CANCELLED){
                continue;
            }
            refundAndCancelWinnerDeal(winnerDeal);
        }

        log.info("낙찰 회원 비활성화 대상 거래 수 - Member ID: {}, Count: {}", memberId, winnerDeals.size());
    }

*/
    @Override
    @Transactional
    public void registerShippingAddress(Long winnerDealId, Long memberId, WinnerDealShippingAddressRequest request) {
        WinnerDeal winnerDeal = winnerDealMapper.findById(winnerDealId);

        if (winnerDeal == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        // 구매자가 아닌 경우
        if (!winnerDeal.getWinnerId().equals(memberId)) {
            throw new CustomException(ErrorType.WINNER_DEAL_SHIPPING_ADDRESS_ACCESS_DENIED);
        }

        // 배송지 또는 운송장 정보가 이미 있거나 PAID 상태가 아닌 경우
        if (winnerDeal.getStatus() != WinnerDealStatus.PAID
                || isShippingInfoRegistered(winnerDeal)
                || isTrackingNumberRegistered(winnerDeal)) {
            throw new CustomException(ErrorType.WINNER_DEAL_SHIPPING_ADDRESS_REGISTRATION_NOT_ALLOWED);
        }

        int updatedRows = winnerDealMapper.updateShippingAddress(winnerDealId, request);
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.WINNER_DEAL_SHIPPING_ADDRESS_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void registerTrackingNumber(Long winnerDealId, Long memberId, WinnerDealTrackingNumberRequest request) {
        WinnerDeal winnerDeal = winnerDealMapper.findById(winnerDealId);

        if (winnerDeal == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        // 판매자가 아닌 경우
        if (!winnerDeal.getSellerId().equals(memberId)) {
            throw new CustomException(ErrorType.WINNER_DEAL_TRACKING_NUMBER_ACCESS_DENIED);
        }

        // 운송장 등록은 PAID 상태에서 배송지 정보가 있고 아직 운송장 정보가 없을 때만 허용한다.
        if (winnerDeal.getStatus() != WinnerDealStatus.PAID
                || !isShippingInfoRegistered(winnerDeal)
                || isTrackingNumberRegistered(winnerDeal)) {
            throw new CustomException(ErrorType.WINNER_DEAL_TRACKING_NUMBER_REGISTRATION_NOT_ALLOWED);
        }

        int updatedRows = winnerDealMapper.updateTrackingNumber(winnerDealId, request);
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.WINNER_DEAL_TRACKING_NUMBER_SAVE_FAILED);
        }

        notificationPublisher.publishNotification(
                winnerDeal.getWinnerId(),
                NotificationType.DELIVERY,
                "상품이 발송되었습니다. 운송사: " + request.getCarrier() + ", 송장번호 : " + request.getTrackingNumber(),
                "/winner-deals/" + winnerDealId
        );
    }

    @Override
    @Transactional
    public void registerTrackingNumberByAdmin(Long winnerDealId, WinnerDealTrackingNumberRequest request) {
        WinnerDeal winnerDeal = winnerDealMapper.findById(winnerDealId);
        if (winnerDeal == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        Auction auction = auctionMapper.findById(winnerDeal.getAuctionId());
        if (auction == null) {
            throw new CustomException(ErrorType.AUCTION_NOT_FOUND);
        }

        if (auction.getInspectionYn() != YesNo.YES
                || winnerDeal.getStatus() != WinnerDealStatus.PAID) {
            throw new CustomException(ErrorType.WINNER_DEAL_TRACKING_NUMBER_REGISTRATION_NOT_ALLOWED);
        }

        int updatedRows = winnerDealMapper.updateTrackingNumber(winnerDealId, request);
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.WINNER_DEAL_TRACKING_NUMBER_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void confirmPurchase(Long winnerDealId, Long memberId) {
        WinnerDeal winnerDeal = winnerDealMapper.findById(winnerDealId);

        if (winnerDeal == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        if (!winnerDeal.getWinnerId().equals(memberId)) {
            throw new CustomException(ErrorType.WINNER_DEAL_CONFIRM_ACCESS_DENIED);
        }

        // 구매확정은 구매자 본인의 배송 중 거래에 대해서만 1회 허용한다.
        if (winnerDeal.getStatus() != WinnerDealStatus.SHIPPED
                || winnerDeal.getConfirmedAt() != null) {
            throw new CustomException(ErrorType.WINNER_DEAL_CONFIRM_NOT_ALLOWED);
        }

        int updatedRows = winnerDealMapper.confirmPurchase(winnerDealId, LocalDateTime.now());
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.WINNER_DEAL_CONFIRM_FAILED);
        }

        Long maxBidAmount = bidQueryService.findMaxBidAmountByAuctionAndBidder2(winnerDeal.getAuctionId(), memberId);
        long reservedAmount = maxBidAmount != null && maxBidAmount >= winnerDeal.getWinnerPrice()
                ? maxBidAmount
                : winnerDeal.getWinnerPrice();

        long refundAmount = reservedAmount - winnerDeal.getWinnerPrice();
        if (refundAmount < 0) {
            throw new CustomException(ErrorType.WINNER_DEAL_SETTLEMENT_INVALID);
        } else if (refundAmount > 0) {
            pointService.refundBid(memberId, refundAmount);
        }

        pointService.settleWinnerDeal(winnerDeal.getSellerId(), winnerDeal.getWinnerPrice());

        // 사용자 VIP 등급 재산정
        memberService.recalculateMemberGrade(winnerDeal.getWinnerId());
        memberService.recalculateMemberGrade(winnerDeal.getSellerId());
    }

    private void handleWinnerDeactivation(Long memberId) {
        List<WinnerDeal> winnerDeals = winnerDealQueryService.findByWinnerId(memberId);

        for (WinnerDeal winnerDeal : winnerDeals) {
            if (shouldKeepDealOnDeactivation(winnerDeal)) {
                log.info("Keep winner deal after winner deactivation - WinnerDeal ID: {}, Auction ID: {}",
                        winnerDeal.getId(), winnerDeal.getAuctionId());
                continue;
            }

            refundAndCancelWinnerDeal(winnerDeal);
        }

        log.info("Winner-side deactivation handling finished - Member ID: {}, Count: {}", memberId, winnerDeals.size());
    }

    private void handleSellerDeactivation(Long memberId) {
        List<WinnerDeal> sellerDeals = winnerDealQueryService.findBySellerId(memberId);

        for (WinnerDeal winnerDeal : sellerDeals) {
            if (shouldKeepDealOnDeactivation(winnerDeal)) {
                log.info("Keep winner deal after seller deactivation - WinnerDeal ID: {}, Auction ID: {}",
                        winnerDeal.getId(), winnerDeal.getAuctionId());
                continue;
            }

            refundAndCancelWinnerDeal(winnerDeal);
        }

        log.info("Seller-side deactivation handling finished - Member ID: {}, Count: {}", memberId, sellerDeals.size());
    }

    private boolean shouldKeepDealOnDeactivation(WinnerDeal winnerDeal) {
        if (winnerDeal.getStatus() == WinnerDealStatus.CANCELLED) {
            return true;
        }

        return isShippingInfoRegistered(winnerDeal);
    }

    private void refundAndCancelWinnerDeal(WinnerDeal winnerDeal) {
        if (winnerDeal.getStatus() == WinnerDealStatus.CANCELLED) {
            throw new CustomException(ErrorType.WINNER_DEAL_ALREADY_CANCELLED);
        }

        int updatedRows = winnerDealMapper.updateStatus(winnerDeal.getId(), "CANCELLED");
        if (updatedRows != 1) {
            throw new CustomException(ErrorType.WINNER_DEAL_UPDATE_FAILED);
        }

        // 낙찰 취소 환불은 비크리 낙찰가가 아니라 낙찰자가 실제 예치한 최고 입찰금 기준으로 처리
        Long depositedAmount = bidQueryService.findMaxBidAmountByAuctionAndBidderRegardlessStatus(
                winnerDeal.getAuctionId(),
                winnerDeal.getWinnerId()
        );

        if (depositedAmount == null || depositedAmount <= 0) {
            throw new CustomException(ErrorType.WINNER_DEAL_BID_AMOUNT_NOT_FOUND);
        }

        pointService.refundBid(winnerDeal.getWinnerId(), depositedAmount);
    }

    private void publishAuctionPriceReferenceSyncRequestedEvent(Auction auction, AuctionItem auctionItem, Long winnerPrice) {
        if (auctionItem == null) {
            log.warn("Skip auction price reference sync because auction item is missing. auctionId={}, itemId={}", auction.getId(), auction.getItemId());
            return;
        }

        eventPublisher.publishEvent(AuctionPriceReferenceSyncRequestedEvent.builder()
                .auctionId(auction.getId())
                .itemId(auctionItem.getId())
                .categoryId(auctionItem.getCategoryId())
                .brand(auctionItem.getBrand())
                .name(auctionItem.getName())
                .quality(auctionItem.getQuality())
                .description(auctionItem.getDescription())
                .winnerPrice(winnerPrice)
                .completedAt(LocalDateTime.now())
                .build());
    }

    private boolean isShippingInfoRegistered(WinnerDeal winnerDeal) {
        // null, 빈 문자열, 공백만 있는 값은 미입력으로 본다.
        return StringUtils.hasText(winnerDeal.getRecipient())
                && StringUtils.hasText(winnerDeal.getTel())
                && StringUtils.hasText(winnerDeal.getZipcode())
                && StringUtils.hasText(winnerDeal.getAddress());
    }

    private boolean isTrackingNumberRegistered(WinnerDeal winnerDeal) {
        // null, 빈 문자열, 공백만 있는 값은 미입력으로 본다.
        return StringUtils.hasText(winnerDeal.getCarrier())
                && StringUtils.hasText(winnerDeal.getTrackingNumber());
    }

    private String generateDealNumber() {
        for (int attempt = 0; attempt < DEAL_NUMBER_MAX_RETRY_COUNT; attempt++) {
            String datePart = LocalDateTime.now().format(DEAL_NUMBER_DATE_FORMATTER);
            String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            String dealNumber = "WD-" + datePart + "-" + randomPart;

            if (!winnerDealMapper.existsByDealNumber(dealNumber)) {
                return dealNumber;
            }
        }

        throw new CustomException(ErrorType.WINNER_DEAL_UPDATE_FAILED);
    }
}

