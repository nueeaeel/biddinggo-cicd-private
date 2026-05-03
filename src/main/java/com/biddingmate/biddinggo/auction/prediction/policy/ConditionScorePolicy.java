package com.biddingmate.biddinggo.auction.prediction.policy;

/**
 * 상품 상태 문자열을 예측 보정용 condition score로 변환하는 정책 인터페이스.
 */
public interface ConditionScorePolicy {
    /**
     * 품질 문자열을 숫자 점수로 변환한다.
     * 지원하지 않는 문자열이면 null을 반환한다.
     */
    Double resolve(String quality);

    /**
     * 현재 정책이 해당 품질 문자열을 해석할 수 있는지 확인한다.
     */
    default boolean supports(String quality) {
        return resolve(quality) != null;
    }
}
