package com.biddingmate.biddinggo.auction.prediction.model;

/**
 * 예측가 계산 결과가 성공했는지, 또는 어떤 사유로 제공되지 않았는지 표현하는 코드.
 * 상세 조회 응답의 상태 해석과 운영 로그 집계를 위해 사용한다.
 */
public enum AuctionPricePredictionReasonCode {
    PREDICTION_AVAILABLE,
    NOT_ENOUGH_REFERENCES,
    EMBEDDING_NOT_FOUND,
    CATEGORY_NOT_FOUND,
    INVALID_CONDITION,
    SUPABASE_TIMEOUT,
    SUPABASE_UNAVAILABLE,
    OUTLIER_FILTERED_ALL,
    FALLBACK_APPLIED
}
