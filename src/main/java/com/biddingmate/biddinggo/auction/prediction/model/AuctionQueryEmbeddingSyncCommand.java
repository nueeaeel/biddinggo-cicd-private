package com.biddingmate.biddinggo.auction.prediction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 조회용 임베딩 동기화 서비스에 전달되는 내부 커맨드.
 * 이벤트에서 받은 원본 값들을 정규화한 뒤 실제 연동 계층으로 넘기는 용도다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionQueryEmbeddingSyncCommand {
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private String quality;
    private Double conditionScore;
    private String embeddingText;
    private AuctionEmbeddingSyncTrigger trigger;
}
