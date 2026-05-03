package com.biddingmate.biddinggo.auction.prediction.client;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingResult;

/**
 * 상품 텍스트를 실제 임베딩 벡터로 변환하는 외부 모델 연동 경계.
 */
public interface AuctionEmbeddingClient {
    /**
     * 현재 환경에서 임베딩 생성 연동이 가능한지 확인한다.
     */
    boolean isEnabled();

    /**
     * 입력 텍스트로부터 임베딩 벡터와 메타데이터를 생성한다.
     */
    AuctionEmbeddingResult createEmbedding(String input);
}
