package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.dto.AuctionPricePredictionResponse;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPricePredictionQuery;

/**
 * 저장된 query embedding과 과거 낙찰 reference를 이용해 예측가를 계산하는 서비스.
 */
public interface AuctionPricePredictionService {
    /**
     * 입력 경매에 대한 예측가를 계산한다.
     */
    AuctionPricePredictionResponse predict(AuctionPricePredictionQuery query);
}
