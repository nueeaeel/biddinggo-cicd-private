package com.biddingmate.biddinggo.point.service;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.point.dto.ExchangePointRequest;
import com.biddingmate.biddinggo.point.dto.MyPointResponse;
import com.biddingmate.biddinggo.point.model.PointHistory;

import java.util.List;

public interface PointService {
    int addPointHistory(PointHistory pointHistory);

    MyPointResponse findMyPointList(BasePageRequest request, Long memberId);
    void exchangePoint(ExchangePointRequest request, Long memberId);
    void refundBid(Long bidderId, Long amount);
    void settleWinnerDeal(Long sellerId, Long amount);

    // 취소된 경매의 입찰 참여자들 환불
    void refundBidsByAuctionIds(List<Long> auctionIds);
}
