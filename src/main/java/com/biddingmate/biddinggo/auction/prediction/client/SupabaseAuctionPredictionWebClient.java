package com.biddingmate.biddinggo.auction.prediction.client;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReference;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceMatch;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbedding;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbeddingMatch;
import com.biddingmate.biddinggo.config.AuctionPredictionSupabaseProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Supabase REST RPC를 호출해 벡터 저장소 레코드를 upsert 하는 구현체.
 * 현재는 query embedding 저장이 먼저 사용되고, price reference 저장은 다음 단계에서 이어진다.
 */
@Component
@RequiredArgsConstructor
public class SupabaseAuctionPredictionWebClient implements AuctionPredictionSupabaseClient {
    private final WebClient webClient;
    private final AuctionPredictionSupabaseProperties properties;

    @Override
    /**
     * Supabase URL과 service role key가 준비된 경우에만 연동 가능 상태로 본다.
     */
    public boolean isEnabled() {
        return properties.isEnabled()
                && StringUtils.hasText(properties.getUrl())
                && StringUtils.hasText(properties.getServiceRoleKey());
    }

    @Override
    /**
     * upsert_auction_query_embedding RPC 함수를 호출해 조회용 임베딩을 저장한다.
     */
    public void upsertAuctionQueryEmbedding(AuctionQueryEmbedding auctionQueryEmbedding) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction prediction supabase client is not configured.");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("p_auction_id", auctionQueryEmbedding.getAuctionId());
        requestBody.put("p_item_id", auctionQueryEmbedding.getItemId());
        requestBody.put("p_category_id", auctionQueryEmbedding.getCategoryId());
        requestBody.put("p_embedding", toVectorLiteral(auctionQueryEmbedding.getEmbedding()));
        requestBody.put("p_embedding_model", auctionQueryEmbedding.getEmbeddingModel());
        requestBody.put("p_embedding_dimension", auctionQueryEmbedding.getEmbeddingDimension());
        requestBody.put("p_embedding_text", auctionQueryEmbedding.getEmbeddingText());

        invokeRpc("upsert_auction_query_embedding", requestBody);
    }

    @Override
    /**
     * auction_query_embedding 테이블에서 특정 경매의 최신 query embedding을 조회한다.
     */
    public AuctionQueryEmbedding findAuctionQueryEmbedding(Long auctionId) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction prediction supabase client is not configured.");
        }

        List<QueryEmbeddingRow> rows = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(extractHost(properties.getUrl()))
                        .path("/rest/v1/auction_query_embedding")
                        .queryParam("select", "id,auction_id,item_id,category_id,embedding,embedding_model,embedding_dimension,embedding_text,created_at,updated_at")
                        .queryParam("auction_id", "eq." + auctionId)
                        .queryParam("limit", 1)
                        .build())
                .headers(this::applySupabaseHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<QueryEmbeddingRow>>() {
                })
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .block();

        if (rows == null || rows.isEmpty()) {
            return null;
        }

        QueryEmbeddingRow row = rows.get(0);

        return AuctionQueryEmbedding.builder()
                .id(row.id())
                .auctionId(row.auctionId())
                .itemId(row.itemId())
                .categoryId(row.categoryId())
                .embedding(parseVector(row.embedding()))
                .embeddingModel(row.embeddingModel())
                .embeddingDimension(row.embeddingDimension())
                .embeddingText(row.embeddingText())
                .createdAt(parseSupabaseDateTime(row.createdAt()))
                .updatedAt(parseSupabaseDateTime(row.updatedAt()))
                .build();
    }

    @Override
    /**
     * upsert_auction_price_reference RPC 함수를 호출해 낙찰 reference를 저장한다.
     */
    public void upsertAuctionPriceReference(AuctionPriceReference auctionPriceReference) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction prediction supabase client is not configured.");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("p_auction_id", auctionPriceReference.getAuctionId());
        requestBody.put("p_item_id", auctionPriceReference.getItemId());
        requestBody.put("p_category_id", auctionPriceReference.getCategoryId());
        requestBody.put("p_winner_price", auctionPriceReference.getWinnerPrice());
        requestBody.put("p_quality", auctionPriceReference.getQuality());
        requestBody.put("p_condition_score", auctionPriceReference.getConditionScore());
        requestBody.put("p_embedding", toVectorLiteral(auctionPriceReference.getEmbedding()));
        requestBody.put("p_embedding_model", auctionPriceReference.getEmbeddingModel());
        requestBody.put("p_embedding_dimension", auctionPriceReference.getEmbeddingDimension());
        requestBody.put("p_embedding_text", auctionPriceReference.getEmbeddingText());
        requestBody.put("p_completed_at", formatSupabaseTimestamp(auctionPriceReference.getCompletedAt()));

        invokeRpc("upsert_auction_price_reference", requestBody);
    }

    @Override
    /**
     * match_auction_price_reference RPC를 호출해 유사 낙찰 reference를 검색한다.
     */
    public List<AuctionPriceReferenceMatch> matchAuctionPriceReferences(
            List<Double> queryEmbedding,
            Long categoryId,
            Double minConditionScore,
            Double maxConditionScore,
            Integer matchCount,
            Double minSimilarity,
            Long excludeAuctionId
    ) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction prediction supabase client is not configured.");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("query_embedding", toVectorLiteral(queryEmbedding));
        requestBody.put("filter_category_id", categoryId);
        requestBody.put("min_condition_score", minConditionScore);
        requestBody.put("max_condition_score", maxConditionScore);
        requestBody.put("match_count", matchCount);
        requestBody.put("min_similarity", minSimilarity);
        requestBody.put("exclude_auction_id", excludeAuctionId);

        List<ReferenceMatchRow> rows = webClient.post()
                .uri(normalizeBaseUrl(properties.getUrl()) + "/rest/v1/rpc/match_auction_price_reference")
                .headers(this::applySupabaseHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ReferenceMatchRow>>() {
                })
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .block();

        if (rows == null) {
            return List.of();
        }

        return rows.stream()
                .filter(Objects::nonNull)
                .map(row -> AuctionPriceReferenceMatch.builder()
                        .referenceId(row.referenceId())
                        .auctionId(row.auctionId())
                        .itemId(row.itemId())
                        .categoryId(row.categoryId())
                        .winnerPrice(row.winnerPrice())
                        .conditionScore(row.conditionScore())
                        .similarity(row.similarity())
                        .build())
                .toList();
    }

    @Override
    public List<AuctionQueryEmbeddingMatch> matchAuctionQueryEmbeddings(
            List<Double> queryEmbedding,
            Integer matchCount,
            Double minSimilarity
    ) {
        if (!isEnabled()) {
            throw new IllegalStateException("Auction prediction supabase client is not configured.");
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        // pgvector RPC 함수가 기대하는 형식으로 query embedding과 검색 조건을 전달한다.
        requestBody.put("query_embedding", toVectorLiteral(queryEmbedding));
        requestBody.put("match_count", matchCount);
        requestBody.put("min_similarity", minSimilarity);

        List<QueryEmbeddingMatchRow> rows = webClient.post()
                .uri(normalizeBaseUrl(properties.getUrl()) + "/rest/v1/rpc/match_auction_query_embedding")
                .headers(this::applySupabaseHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<QueryEmbeddingMatchRow>>() {
                })
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .block();

        if (rows == null) {
            return List.of();
        }

        // Supabase에서는 후보 ID 집합만 받고, 실제 경매 조회는 MariaDB에서 이어서 수행한다.
        return rows.stream()
                .filter(Objects::nonNull)
                .map(row -> AuctionQueryEmbeddingMatch.builder()
                        .auctionId(row.auctionId())
                        .itemId(row.itemId())
                        .categoryId(row.categoryId())
                        .build())
                .toList();
    }

    /**
     * 지정한 Supabase RPC 함수를 공통 방식으로 호출한다.
     */
    private void invokeRpc(String functionName, Map<String, Object> requestBody) {
        webClient.post()
                .uri(normalizeBaseUrl(properties.getUrl()) + "/rest/v1/rpc/" + functionName)
                .headers(headers -> applySupabaseHeaders(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .block();
    }

    /**
     * Supabase REST 호출에 필요한 인증/스키마 헤더를 설정한다.
     */
    private void applySupabaseHeaders(HttpHeaders headers) {
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getServiceRoleKey());
        headers.set("apikey", properties.getServiceRoleKey());
        headers.set("Content-Profile", properties.getSchema());
        headers.set("Accept-Profile", properties.getSchema());
    }

    /**
     * Java List 벡터를 pgvector 입력 형식 문자열로 변환한다.
     */
    private String toVectorLiteral(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding vector must not be empty.");
        }

        return embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private List<Double> parseVector(Object embeddingValue) {
        if (embeddingValue == null) {
            return List.of();
        }

        if (embeddingValue instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(value -> ((Number) value).doubleValue())
                    .toList();
        }

        String raw = String.valueOf(embeddingValue).trim();

        if (raw.length() < 2 || raw.charAt(0) != '[' || raw.charAt(raw.length() - 1) != ']') {
            return List.of();
        }

        String body = raw.substring(1, raw.length() - 1).trim();

        if (body.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(body.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Double::parseDouble)
                .toList();
    }

    private LocalDateTime parseSupabaseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException exception) {
            return LocalDateTime.parse(value);
        }
    }

    private String formatSupabaseTimestamp(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return value.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * base URL 마지막 슬래시를 제거해 경로 조합을 안정화한다.
     */
    private String normalizeBaseUrl(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String extractHost(String baseUrl) {
        String normalizedBaseUrl = normalizeBaseUrl(baseUrl);
        return normalizedBaseUrl.replaceFirst("^https?://", "");
    }

    private record QueryEmbeddingRow(
            Long id,
            @JsonAlias("auction_id") Long auctionId,
            @JsonAlias("item_id") Long itemId,
            @JsonAlias("category_id") Long categoryId,
            Object embedding,
            @JsonAlias("embedding_model") String embeddingModel,
            @JsonAlias("embedding_dimension") Integer embeddingDimension,
            @JsonAlias("embedding_text") String embeddingText,
            @JsonAlias("created_at") String createdAt,
            @JsonAlias("updated_at") String updatedAt
    ) {
    }

    private record ReferenceMatchRow(
            @JsonAlias("reference_id") Long referenceId,
            @JsonAlias("auction_id") Long auctionId,
            @JsonAlias("item_id") Long itemId,
            @JsonAlias("category_id") Long categoryId,
            @JsonAlias("winner_price") Long winnerPrice,
            @JsonAlias("condition_score") Double conditionScore,
            Double similarity
    ) {
    }

    private record QueryEmbeddingMatchRow(
            @JsonAlias("auction_id") Long auctionId,
            @JsonAlias("item_id") Long itemId,
            @JsonAlias("category_id") Long categoryId
    ) {
    }
}
