package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Supabase 유사 reference 검색 결과를 담는 모델.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionPriceReferenceMatch {
    private Long referenceId;
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private Long winnerPrice;
    private Double conditionScore;
    private Double similarity;
}
