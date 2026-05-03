package com.biddingmate.biddinggo.bid.service;

import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.bid.dto.CreateBidRequest;
import com.biddingmate.biddinggo.bid.dto.CreateBidResponse;
import com.biddingmate.biddinggo.bid.model.Bid;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.service.MemberService;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import com.biddingmate.biddinggo.notification.service.NotificationPublisher;
import com.biddingmate.biddinggo.point.model.PointHistory;
import com.biddingmate.biddinggo.point.model.PointHistoryType;
import com.biddingmate.biddinggo.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
    입찰 전체 흐름을 조율하는 애플리케이션 서비스.
    트랜잭션 경계는 이 클래스에서만 관리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidApplicationServiceImpl implements BidApplicationService {
    private final AuctionMapper auctionMapper;
    private final MemberMapper memberMapper;
    private final MemberService memberService;
    private final PointService pointService;
    private final BidService bidService;
    private final BidQueryService bidQueryService;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public CreateBidResponse createBidProcess(Long memberId, CreateBidRequest request){
        Long auctionId = request.getAuctionId();
        Long preciousTopBidderId = bidQueryService.findTopBidderId(auctionId);

        // 1. 경매 유효성 검증
        Auction auction = auctionMapper.findByIdForUpdate(auctionId);

        if(auction == null) {
            throw new CustomException(ErrorType.AUCTION_NOT_FOUND);
        }

        if(auction.getSellerId().equals(memberId)){
            throw new CustomException(ErrorType.CANNOT_BID_ON_OWN_AUCTION);
        }

        LocalDateTime now = LocalDateTime.now();

        if (auction.getStatus() != AuctionStatus.ON_GOING ||
                now.isBefore(auction.getStartDate()) ||
                now.isAfter(auction.getEndDate())) {
            throw new CustomException(ErrorType.AUCTION_NOT_BIDDABLE);
        }

        if (auction.getExtensionYn() == YesNo.NO &&
                !now.isBefore(auction.getEndDate().minusMinutes(1)) &&
                now.isBefore(auction.getEndDate())) {

            int updated = auctionMapper.extendAuctionTime(auctionId);

            if (updated == 1) {
                log.info("경매 ID {} : 종료 임박 입찰로 인해 시간이 3분 연장되었습니다.", auctionId);
                auction.setEndDate(auction.getEndDate().plusMinutes(3));
                auction.setExtensionYn(YesNo.YES);
            }else {
                // 동시성 이슈로 인한 연장 실패 시 입찰 취소 처리
                throw new CustomException(ErrorType.AUCTION_ALREADY_FINISHED);
            }
        }

        Long lastBidAmount = bidService.getLastBidAmount(memberId, auctionId);
        Long additionalBidAmount = request.getAmount() - lastBidAmount;
        Long bidderPoint = memberMapper.getPointById(memberId);
        if(bidderPoint == null || additionalBidAmount > bidderPoint){
            throw new CustomException(ErrorType.NOT_ENOUGH_POINT);
        }


        // 2. Bid 등록
        Bid bid = bidService.createBid(memberId, auction, request);


        // 3. 입찰자 포인트 차감
        // 추후 MemberService 구현 이후 변경
        memberService.deductPoint(memberId, additionalBidAmount);

        // 4. Auction 정보 갱신 : 경매 차순위 값 변경 + 입찰 수 증가
        // 추후 AuctionService 구현 이후 변경
        Bid vickreyBid = bidService.getVickreyBid(auctionId);
        auctionMapper.updateAfterBid(
                auctionId,
                vickreyBid != null ? vickreyBid.getAmount() : null
        );

        // 5. Point History 저장
        // 추후 PointHistoryService 구현 이후 변경
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .bidId(bid.getId())
                .type(PointHistoryType.BID)
                .amount(additionalBidAmount)
                .createdAt(LocalDateTime.now())
                .build();
        int pointInsert = pointService.addPointHistory(pointHistory);

        if (pointInsert != 1) {
            throw new CustomException(ErrorType.POINT_HISTORY_SAVE_FAILED);
        }

        // 입찰 알람
        notificationPublisher.publishNotification(
                auction.getSellerId(),
                NotificationType.NEW_BID,
                "경매 #" + auctionId + "에 새로운 입찰이 등록되었습니다.",
                "/auctions/" + auctionId
        );

        Long currentTopBidderId = bidQueryService.findTopBidderId(auctionId);

        // 알람 분기
        // 현재 최고입찰자가 존재하고 그 최고 입찰자가 "지금 입찰한 사람이고" 이전에는 그 사람이 최고 입찰자가 아니었을 때
        // 이번 입찰로 새롭게 1등이 된 경우만 최상위 입찰 알람을 보낸다. (원래 1등이던 사람이 금액만 올린 경우는 중복 알림 방지)
        if (currentTopBidderId != null && currentTopBidderId.equals(memberId)
                && (preciousTopBidderId == null || !preciousTopBidderId.equals(memberId))) {

            // 최상위 입찰 알람
            notificationPublisher.publishNotification(
                    memberId,
                    NotificationType.TOP_BID,
                    "경매 #" + auctionId + "에서 현재 최상위 입찰자가 되었습니다.",
                    "/auctions/" + auctionId
            );

        }

        return CreateBidResponse.builder()
                .bidId(bid.getId())
                .build();
    }
}
