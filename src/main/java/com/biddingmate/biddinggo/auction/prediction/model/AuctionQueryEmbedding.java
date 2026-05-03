package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 현재 경매/상품에 대한 조회용 임베딩 정보를 표현하는 모델.
 * MariaDB 엔티티가 아니라 Supabase 벡터 저장소에 적재할 payload 기준으로 사용한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionQueryEmbedding {
    private Long id;
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private List<Double> embedding;
    private String embeddingModel;
    private Integer embeddingDimension;
    private String embeddingText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
