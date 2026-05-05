package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 외부 임베딩 모델 호출 결과를 담는 모델.
 * 생성된 벡터와 모델명, 차원 수를 함께 전달한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionEmbeddingResult {
    private List<Double> embedding;
    private String model;
    private Integer dimension;
}
