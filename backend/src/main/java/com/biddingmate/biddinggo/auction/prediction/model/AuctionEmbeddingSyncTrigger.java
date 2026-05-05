package com.biddingmate.biddinggo.auction.prediction.model;

/**
 * 조회용 임베딩 동기화가 어떤 비즈니스 이벤트에 의해 발생했는지 구분한다.
 */
public enum AuctionEmbeddingSyncTrigger {
    CREATED,
    UPDATED
}
