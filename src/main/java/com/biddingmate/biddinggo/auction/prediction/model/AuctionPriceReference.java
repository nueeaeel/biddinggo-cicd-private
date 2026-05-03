package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Supabase의 과거 낙찰 reference 레코드를 애플리케이션에서 다루기 위한 모델.
 * 예측 계산 시 비교 대상이 되는 낙찰 완료 데이터의 핵심 속성을 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionPriceReference {
    private Long id;
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private Long winnerPrice;
    private String quality;
    private Double conditionScore;
    private List<Double> embedding;
    private String embeddingModel;
    private Integer embeddingDimension;
    private String embeddingText;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
