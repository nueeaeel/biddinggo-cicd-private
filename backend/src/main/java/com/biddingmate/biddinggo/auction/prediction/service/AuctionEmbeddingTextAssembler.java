package com.biddingmate.biddinggo.auction.prediction.service;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * 임베딩 생성에 사용할 원문 텍스트를 조합하는 도우미.
 * 현재는 브랜드, 상품명, 설명을 이어 붙여 검색 중심 텍스트를 만든다.
 */
@Component
public class AuctionEmbeddingTextAssembler {
    /**
     * 비어 있지 않은 필드만 이어 붙여 하나의 임베딩 입력 문장을 만든다.
     */
    public String assemble(String brand, String name, String description) {
        return Stream.of(brand, name, description)
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalize)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    /**
     * 연속 공백을 하나로 줄여 임베딩 입력값을 안정적으로 맞춘다.
     */
    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }
}
