package com.biddingmate.biddinggo.auction.service;

import com.biddingmate.biddinggo.auction.dto.CreateAuctionFromInspectionItemRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionRequest;
import com.biddingmate.biddinggo.auction.dto.UpdateAuctionRequest;
import com.biddingmate.biddinggo.auction.event.AuctionCancelledEvent;
import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.auction.prediction.event.AuctionQueryEmbeddingSyncRequestedEvent;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingSyncTrigger;
import com.biddingmate.biddinggo.bid.model.Bid;
import com.biddingmate.biddinggo.bid.service.BidQueryService;
import com.biddingmate.biddinggo.bid.service.BidService;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import com.biddingmate.biddinggo.member.service.MemberService;
import com.biddingmate.biddinggo.item.service.AuctionItemService;
import com.biddingmate.biddinggo.point.service.PointService;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * auction 엔티티 생성만 담당하는 서비스 구현체.
 * 트랜잭션은 상위 애플리케이션 서비스에서 시작된 흐름에 참여한다.
 */
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionMapper auctionMapper;
    private final AuctionItemService auctionItemService;
    private final BidQueryService bidQueryService;
    private final BidService bidService;
    private final PointService pointService;
    private final ApplicationEventPublisher eventPublisher;
    private final WinnerDealService winnerDealService;
    private final MemberService memberService;

    @Override
    @Transactional
    /**
     * 경매 수정 메인 로직.
     * 판매자 본인 여부와 수정 가능 상태를 확인한 뒤 auction 테이블의 수정 가능 필드만 반영한다.
     */
    public void updateAuction(Long auctionId, UpdateAuctionRequest request, Long sellerId) {
        validateUpdateRequest(auctionId, request, sellerId);

        // 동시 수정 충돌을 줄이기 위해 수정 대상 경매를 lock 조회한다.
        Auction auction = getAuctionForModification(auctionId);
        validateSeller(auction, sellerId);

        // 정책상 PENDING 또는 ON_GOING + bidCount == 0 인 경우에만 수정 가능하다.
        if (!isAuctionUpdatableOrCancelable(auction)) {
            throw new CustomException(ErrorType.AUCTION_UPDATE_NOT_ALLOWED);
        }

        // 현재 정책상 경매 메타 정보(가격/일정)만 수정 대상으로 둔다.
        Auction updateTarget = Auction.builder()
                .id(auctionId)
                .startPrice(request.getStartPrice())
                .bidUnit(request.getBidUnit())
                .buyNowPrice(request.getBuyNowPrice())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        int updatedCount = auctionMapper.updateAuction(updateTarget);

        if (updatedCount != 1) {
            throw new CustomException(ErrorType.AUCTION_UPDATE_NOT_ALLOWED);
        }

        AuctionItem auctionItem = auctionItemService.getAuctionItem(auction.getItemId());
        eventPublisher.publishEvent(AuctionQueryEmbeddingSyncRequestedEvent.builder()
                .auctionId(auctionId)
                .itemId(auctionItem.getId())
                .categoryId(auctionItem.getCategoryId())
                .brand(auctionItem.getBrand())
                .name(auctionItem.getName())
                .quality(auctionItem.getQuality())
                .description(auctionItem.getDescription())
                .trigger(AuctionEmbeddingSyncTrigger.UPDATED)
                .build());
    }

    @Override
    @Transactional
    /**
     * 경매 취소 메인 로직.
     * 판매자 본인 여부와 취소 가능 상태를 확인한 뒤 status와 cancel_date를 함께 갱신한다.
     */
    public void cancelAuction(Long auctionId, Long sellerId) {
        if (auctionId == null || auctionId <= 0 || sellerId == null || sellerId <= 0) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CANCEL_REQUEST);
        }

        // 취소 직전 상태를 정확히 확인하기 위해 lock 조회한다.
        Auction auction = getAuctionForModification(auctionId);
        validateSeller(auction, sellerId);

        // 수정과 동일한 정책으로 취소 가능 여부를 판단한다.
        if (!isAuctionUpdatableOrCancelable(auction)) {
            throw new CustomException(ErrorType.AUCTION_CANCEL_NOT_ALLOWED);
        }

        int updatedCount = auctionMapper.cancelAuction(auctionId, LocalDateTime.now(), AuctionStatus.CANCELLED);

        if (updatedCount != 1) {
            throw new CustomException(ErrorType.AUCTION_CANCEL_NOT_ALLOWED);
        }
    }

    @Override
    @Transactional
    public void buyNowAuction(Long auctionId, Long buyerId) {
        // 유효하지 않은 경매 ID 요청은 즉시 차단한다.
        if (auctionId == null || auctionId <= 0) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }

        // 즉시구매 처리 중 동시성 문제를 막기 위해 수정용 경매 정보를 조회한다.
        Auction auction = getAuctionForModification(auctionId);

        // 판매자는 자신의 경매를 즉시구매할 수 없다.
        if (auction.getSellerId().equals(buyerId)) {
            throw new CustomException(ErrorType.CANNOT_BUY_NOW_OWN_AUCTION);
        }

        // 현재 경매가 즉시구매 가능한 상태인지 확인
        Long buyNowPrice = auction.getBuyNowPrice();
        if (!isAuctionBuyNowAvailable(auction)) {
            throw new CustomException(ErrorType.CANNOT_BUY_NOW);
        }

        // 기존 입찰로 이미 선점한 금액을 반영해 추가로 필요한 포인트만 계산한다.
        Long lastBidAmount = bidService.getLastBidAmount(buyerId, auctionId);
        long additionalAmount = Math.max(buyNowPrice - lastBidAmount, 0L);

        // 즉시구매에 필요한 차액만큼 현재 포인트가 충분한지 검증한다.
        if (additionalAmount > 0 && memberService.getCurrentPoint(buyerId) < additionalAmount) {
            throw new CustomException(ErrorType.NOT_ENOUGH_POINT);
        }

        // 검증이 끝난 뒤 실제 거래 생성과 종료 처리는 winnerDeal 서비스에 위임한다.
        winnerDealService.processBuyNow(auction, buyerId, buyNowPrice, additionalAmount);
    }

    @Override
    @Transactional
    public void handleMemberDeactivationBeforeWinning(Long memberId) {
        // 1. 입찰 무효화
        invalidateBids(memberId);

        // 2. 판매자 경매 취소
        List<Long> sellerAuctionIds = findActiveAuctionsBySeller(memberId);
        cancelAuctionsAndItems(sellerAuctionIds);

        // 3. 입찰 참여수 감소
        decreaseBidCountByDeactiveMember(memberId);

        // 4. 최고 입찰자의 비활성화 -> 비크리 재계산
        recalculateVickreyPriceByBidder(memberId);
    }

    @Override
    public Long createAuction(CreateAuctionRequest request, Long itemId, Long sellerId) {
        if (itemId == null) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }

        // 일반 경매 등록은 검수 경매가 아니므로 inspectionYn은 서버에서 NO로 고정한다.
        Auction auction = Auction.builder()
                .itemId(itemId)
                .sellerId(sellerId)
                .type(request.getAuction().getType())
                .inspectionYn(YesNo.NO)
                .extensionYn(resolveExtensionYn(request.getAuction().getExtensionYn()))
                .startPrice(request.getAuction().getStartPrice())
                .bidUnit(request.getAuction().getBidUnit())
                .vickreyPrice(request.getAuction().getVickreyPrice())
                .buyNowPrice(request.getAuction().getBuyNowPrice())
                .startDate(request.getAuction().getStartDate())
                .endDate(request.getAuction().getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        // auction 저장 후 생성된 PK를 모델에 주입받는다.
        int auctionInsertCount = auctionMapper.insert(auction);

        if (auctionInsertCount != 1 || auction.getId() == null) {
            throw new CustomException(ErrorType.AUCTION_SAVE_FAILED);
        }

        return auction.getId();
    }

    @Override
    public Long createAuction(CreateAuctionFromInspectionItemRequest request, Long sellerId) {
        if (request.getItemId() == null) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }

        // 검수 완료 상품 기반 경매 등록은 inspectionYn을 서버에서 YES로 고정한다.
        Auction auction = Auction.builder()
                .itemId(request.getItemId())
                .sellerId(sellerId)
                .type(request.getAuction().getType())
                .inspectionYn(YesNo.YES)
                .extensionYn(resolveExtensionYn(request.getAuction().getExtensionYn()))
                .startPrice(request.getAuction().getStartPrice())
                .bidUnit(request.getAuction().getBidUnit())
                .vickreyPrice(request.getAuction().getVickreyPrice())
                .buyNowPrice(request.getAuction().getBuyNowPrice())
                .startDate(request.getAuction().getStartDate())
                .endDate(request.getAuction().getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        int auctionInsertCount = auctionMapper.insert(auction);

        if (auctionInsertCount != 1 || auction.getId() == null) {
            throw new CustomException(ErrorType.AUCTION_SAVE_FAILED);
        }

        return auction.getId();
    }

    /**
     * 수정 요청 기본값을 검증한다.
     * 경매 ID, 판매자 ID, 수정 필수 필드가 비어 있으면 요청 오류로 처리한다.
     */
    private void validateUpdateRequest(Long auctionId, UpdateAuctionRequest request, Long sellerId) {
        if (auctionId == null || auctionId <= 0 || request == null) {
            throw new CustomException(ErrorType.INVALID_AUCTION_UPDATE_REQUEST);
        }

        if (sellerId == null || sellerId <= 0) {
            throw new CustomException(ErrorType.INVALID_AUCTION_UPDATE_REQUEST);
        }

        if (request.getStartPrice() == null || request.getBidUnit() == null
                || request.getStartDate() == null || request.getEndDate() == null) {
            throw new CustomException(ErrorType.INVALID_AUCTION_UPDATE_REQUEST);
        }
    }

    /**
     * 수정/취소 대상 경매를 배타적으로 조회한다.
     */
    private Auction getAuctionForModification(Long auctionId) {
        Auction auction = auctionMapper.findByIdForUpdate(auctionId);

        if (auction == null) {
            throw new CustomException(ErrorType.AUCTION_NOT_FOUND);
        }

        return auction;
    }

    /**
     * 요청 판매자와 실제 경매 판매자가 같은지 검증한다.
     */
    private void validateSeller(Auction auction, Long sellerId) {
        if (!auction.getSellerId().equals(sellerId)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }
    }

    /**
     * 경매 수정/취소 가능 여부를 판단한다.
     * PENDING 은 항상 허용하고, ON_GOING 은 입찰 수가 0일 때만 허용한다.
     */
    private boolean isAuctionUpdatableOrCancelable(Auction auction) {
        if (auction.getStatus() == AuctionStatus.PENDING) {
            return true;
        }

        return auction.getStatus() == AuctionStatus.ON_GOING
                && auction.getBidCount() != null
                && auction.getBidCount() == 0;
    }

    private boolean isAuctionBuyNowAvailable(Auction auction) {
        LocalDateTime now = LocalDateTime.now();

        return auction.getBuyNowPrice() != null
                && auction.getStatus() == AuctionStatus.ON_GOING
                && !now.isBefore(auction.getStartDate())
                && !now.isAfter(auction.getEndDate());
    }

    private YesNo resolveExtensionYn(YesNo extensionYn) {
        return extensionYn != null ? extensionYn : YesNo.NO;
    }

    // 입찰 무효화
    private void invalidateBids(Long memberId) {
        bidService.invalidateBidsByMember(memberId);
    }

    // 진행 중 경매 조회
    private List<Long> findActiveAuctionsBySeller(Long memberId) {
        // auctionMapper에서 ON_AUCTION 상태인 경매 ID 조회
        return auctionMapper.findActiveAuctionIdsBySeller(memberId);
    }

    // 진행 중 경매들 강제 취소
    private void cancelAuctionsAndItems(List<Long> auctionIds) {
        if (auctionIds == null || auctionIds.isEmpty()) return;

        // Item 상태를 CANCELLED로 변경 (ItemService 호출
        auctionItemService.cancelItemsByAuctionIds(auctionIds);

        // Auction 상태를 CANCELLED로 변경
        auctionMapper.updateAuctionStatus(auctionIds, AuctionStatus.CANCELLED);

        // 환불 이벤트 생성
        eventPublisher.publishEvent(new AuctionCancelledEvent(auctionIds));
    }

    // 비활성화 유저의 경매 입찰 수 차감
    private void decreaseBidCountByDeactiveMember(Long memberId) {
        auctionMapper.decreaseBidCountByDeactiveMember(memberId);
    }

    // 비크리 입찰가 재계산
    private void recalculateVickreyPriceByBidder(Long memberId) {
        // 비활성화된 사용자의 진행 중인 경매 조회
        List<Long> auctionIds = bidQueryService.findOngoingAuctionIdsByMember(memberId);

        for (Long auctionId : auctionIds) {
            // 비활성화 된 사용자의 최고 입찰 금액 환불
            Long amount = bidQueryService.findMaxBidAmountByAuctionAndBidder(auctionId, memberId);
            pointService.refundBid(memberId, amount);

            // 활성화 중인 상위 2개 입찰 조회 (비크리 핵심)
            List<Bid> topBids = bidQueryService.findTop2ActiveBids(auctionId);

            if (topBids.size() < 2) {
                // 입찰 1위를 제외(비활성화)하고 남은 활성화 사용자들의 입찰이 1명 이하인 경우
                auctionMapper.resetVickreyPriceToStartPrice(auctionId);
                continue;
            }

            Bid second = topBids.get(1); // 비크리 금액의 입찰자(차순위 입찰자)

            // 비크리 차순위 승계
            auctionMapper.updateVickreyPrice(auctionId, second.getAmount());
        }
    }
}
