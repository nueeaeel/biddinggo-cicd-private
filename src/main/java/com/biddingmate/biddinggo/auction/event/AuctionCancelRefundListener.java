package com.biddingmate.biddinggo.auction.event;

import com.biddingmate.biddinggo.auction.dto.RefundDto;
import com.biddingmate.biddinggo.bid.model.Bid;
import com.biddingmate.biddinggo.bid.service.BidQueryService;
import com.biddingmate.biddinggo.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
/*
    경매 취소 시 환불 처리 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class AuctionCancelRefundListener {
    private final PointService pointService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionCancelled(AuctionCancelledEvent event) {
        List<Long> auctionIds = event.getAuctionIds();
        pointService.refundBidsByAuctionIds(auctionIds);
    }
}