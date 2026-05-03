package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 예측가 계산에 필요한 최소 입력값을 담는 모델.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionPricePredictionQuery {
    private Long auctionId;
    private Long categoryId;
    private String quality;
}
