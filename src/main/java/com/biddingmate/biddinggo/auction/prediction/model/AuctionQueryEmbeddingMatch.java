package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * semantic search 1차 후보 추출 결과 모델.
 * 최종 경매 정보는 이 ID들을 기준으로 MariaDB에서 다시 조회한다.
 */
public class AuctionQueryEmbeddingMatch {
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
}
