package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 낙찰 reference 동기화에 필요한 최소 입력값을 담는 모델.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionPriceReferenceSyncCommand {
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private String quality;
    private Double conditionScore;
    private String embeddingText;
    private Long winnerPrice;
    private LocalDateTime completedAt;
}
