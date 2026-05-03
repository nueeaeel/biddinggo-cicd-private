package com.biddingmate.biddinggo.auction.prediction.policy;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 현재 프로젝트에서 사용하는 기본 품질 문자열 매핑 정책.
 * 조회 시 계산 부담을 줄이기 위해 문자열을 단순 수치로 정규화한다.
 */
@Component
public class DefaultConditionScorePolicy implements ConditionScorePolicy {
    private static final Map<String, Double> CONDITION_SCORES = Map.ofEntries(
            Map.entry("새상품", 1.0d),
            Map.entry("S", 0.9d),
            Map.entry("A+", 0.7d),
            Map.entry("A", 0.6d),
            Map.entry("B", 0.4d),
            Map.entry("C", 0.2d)
    );

    @Override
    /**
     * 입력 품질 문자열의 공백을 정리한 뒤 점수 테이블에서 값을 찾는다.
     */
    public Double resolve(String quality) {
        if (quality == null || quality.isBlank()) {
            return null;
        }

        return CONDITION_SCORES.get(normalize(quality));
    }

    /**
     * 품질 문자열 비교 시 불필요한 공백 차이로 인한 오차를 줄인다.
     */
    private String normalize(String quality) {
        return quality.trim().replaceAll("\\s+", "");
    }
}
