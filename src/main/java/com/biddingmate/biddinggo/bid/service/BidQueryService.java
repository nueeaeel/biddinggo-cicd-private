package com.biddingmate.biddinggo.bid.service;

import com.biddingmate.biddinggo.auction.dto.BidCountDto;
import com.biddingmate.biddinggo.auction.dto.RefundDto;
import com.biddingmate.biddinggo.bid.model.Bid;

import java.util.List;

public interface BidQueryService {
    List<Long> findOngoingAuctionIdsByMember(Long memberId);
    Long findTopBidderId(Long auctionId);
    List<Bid> findTop2ActiveBids(Long auctionId);

    List<RefundDto> findByAuctionIds(List<Long> auctionIds);

    Long findMaxBidAmountByAuctionAndBidder(Long auctionId, Long memberId);

    Long findMaxBidAmountByAuctionAndBidder2(Long auctionId, Long memberId);

    Long findMaxBidAmountByAuctionAndBidderRegardlessStatus(Long auctionId, Long bidderId);

    List<RefundDto> findRefundTargetsExcludingWinner(Long auctionId, Long winnerId);
}
