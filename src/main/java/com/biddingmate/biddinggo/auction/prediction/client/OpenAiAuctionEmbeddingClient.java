package com.biddingmate.biddinggo.auction.prediction.client;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingResult;
import com.biddingmate.biddinggo.config.AuctionPredictionEmbeddingProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 호환 embeddings API를 호출해 상품 텍스트를 벡터로 변환하는 구현체.
 * 현재는 조회용 query embedding 생성에 사용된다.
 */
@Component
@RequiredArgsConstructor
public class OpenAiAuctionEmbeddingClient implements AuctionEmbeddingClient {
    private final WebClient webClient;
    private final AuctionPredictionEmbeddingProperties properties;

    @Override
    /**
     * 필수 설정값이 모두 채워졌을 때만 연동 가능 상태로 본다.
     */
    public boolean isEnabled() {
        return properties.isEnabled()
                && StringUtils.hasText(properties.getBaseUrl())
                && StringUtils.hasText(properties.getApiKey())
                && StringUtils.hasText(properties.getModel());
    }

    @Override
    /**
     * 외부 embeddings API를 호출해 입력 텍스트를 벡터로 변환한다.
     */
    public AuctionEmbeddingResult createEmbedding(String input) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction embedding client is not configured.");
        }

        if (!StringUtils.hasText(input)) {
            throw new IllegalArgumentException("Embedding input must not be blank.");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("input", input);
        requestBody.put("model", properties.getModel());

        if (properties.getDimensions() != null && properties.getDimensions() > 0) {
            requestBody.put("dimensions", properties.getDimensions());
        }

        EmbeddingApiResponse response = webClient.post()
                .uri(normalizeBaseUrl(properties.getBaseUrl()) + "/embeddings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(EmbeddingApiResponse.class)
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .block();

        if (response == null || response.data() == null || response.data().isEmpty() || response.data().get(0).embedding() == null) {
            throw new IllegalStateException("Embedding response is empty.");
        }

        List<Double> embedding = response.data().get(0).embedding();

        return AuctionEmbeddingResult.builder()
                .embedding(embedding)
                .model(response.model() != null ? response.model() : properties.getModel())
                .dimension(embedding.size())
                .build();
    }

    /**
     * base URL 마지막 슬래시를 제거해 경로 조합을 안정화한다.
     */
    private String normalizeBaseUrl(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EmbeddingApiResponse(List<EmbeddingData> data, String model) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EmbeddingData(List<Double> embedding) {
    }
}
