package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.dto.AuctionPricePredictionResponse;
import com.biddingmate.biddinggo.auction.prediction.client.AuctionPredictionSupabaseClient;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPricePredictionQuery;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPricePredictionReasonCode;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceMatch;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbedding;
import com.biddingmate.biddinggo.auction.prediction.policy.ConditionScorePolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * category, condition score, cosine similarity를 조합해 예측가를 계산하는 기본 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAuctionPricePredictionService implements AuctionPricePredictionService {
    private final AuctionPredictionSupabaseClient auctionPredictionSupabaseClient;
    private final ConditionScorePolicy conditionScorePolicy;

    @Value("${auction.prediction.price.min-reference-count:1}")
    private int minReferenceCount;

    @Value("${auction.prediction.price.top-k:20}")
    private int topK;

    @Value("${auction.prediction.price.min-similarity:0.55}")
    private double minSimilarity;

    @Value("${auction.prediction.price.condition-score-tolerance:0.20}")
    private double conditionScoreTolerance;

    @Value("${auction.prediction.price.outlier-multiplier:1.5}")
    private double outlierMultiplier;

    @Value("${auction.prediction.price.min-weight:0.000001}")
    private double minWeight;

    @Override
    public AuctionPricePredictionResponse predict(AuctionPricePredictionQuery query) {
        if (query == null || query.getAuctionId() == null || query.getAuctionId() <= 0 || query.getCategoryId() == null || query.getCategoryId() <= 0) {
            return unavailable(AuctionPricePredictionReasonCode.CATEGORY_NOT_FOUND, false, null);
        }

        if (!auctionPredictionSupabaseClient.isEnabled()) {
            return unavailable(AuctionPricePredictionReasonCode.SUPABASE_UNAVAILABLE, true, null);
        }

        Double conditionScore = conditionScorePolicy.resolve(query.getQuality());

        if (conditionScore == null) {
            return unavailable(AuctionPricePredictionReasonCode.INVALID_CONDITION, false, null);
        }

        try {
            AuctionQueryEmbedding auctionQueryEmbedding = auctionPredictionSupabaseClient.findAuctionQueryEmbedding(query.getAuctionId());

            if (auctionQueryEmbedding == null || auctionQueryEmbedding.getEmbedding() == null || auctionQueryEmbedding.getEmbedding().isEmpty()) {
                return unavailable(AuctionPricePredictionReasonCode.EMBEDDING_NOT_FOUND, false, null);
            }

            List<AuctionPriceReferenceMatch> matches = auctionPredictionSupabaseClient.matchAuctionPriceReferences(
                    auctionQueryEmbedding.getEmbedding(),
                    query.getCategoryId(),
                    Math.max(0d, conditionScore - conditionScoreTolerance),
                    Math.min(1d, conditionScore + conditionScoreTolerance),
                    topK,
                    minSimilarity,
                    query.getAuctionId()
            );

            if (matches == null || matches.size() < minReferenceCount) {
                return unavailable(AuctionPricePredictionReasonCode.NOT_ENOUGH_REFERENCES, false, matches != null ? matches.size() : 0);
            }

            List<AuctionPriceReferenceMatch> filteredMatches = filterOutliers(matches);

            if (filteredMatches.isEmpty()) {
                return unavailable(AuctionPricePredictionReasonCode.OUTLIER_FILTERED_ALL, false, 0);
            }

            if (filteredMatches.size() < minReferenceCount) {
                return unavailable(AuctionPricePredictionReasonCode.NOT_ENOUGH_REFERENCES, false, filteredMatches.size());
            }

            long predictedPrice = Math.round(weightedAverage(filteredMatches));
            double confidence = averageSimilarity(filteredMatches);

            return AuctionPricePredictionResponse.builder()
                    .predictedPrice(predictedPrice)
                    .referenceCount(filteredMatches.size())
                    .confidence(roundToTwoDecimals(confidence))
                    .fallbackUsed(false)
                    .reasonCode(AuctionPricePredictionReasonCode.PREDICTION_AVAILABLE.name())
                    .build();
        } catch (RuntimeException exception) {
            log.error("Failed to predict auction price. auctionId={}, categoryId={}", query.getAuctionId(), query.getCategoryId(), exception);
            if (isTimeout(exception)) {
                return unavailable(AuctionPricePredictionReasonCode.SUPABASE_TIMEOUT, true, null);
            }

            return unavailable(AuctionPricePredictionReasonCode.SUPABASE_UNAVAILABLE, true, null);
        }
    }

    private AuctionPricePredictionResponse unavailable(AuctionPricePredictionReasonCode reasonCode, boolean fallbackUsed, Integer referenceCount) {
        return AuctionPricePredictionResponse.builder()
                .predictedPrice(null)
                .referenceCount(referenceCount)
                .confidence(null)
                .fallbackUsed(fallbackUsed)
                .reasonCode(reasonCode.name())
                .build();
    }

    private List<AuctionPriceReferenceMatch> filterOutliers(List<AuctionPriceReferenceMatch> matches) {
        if (matches.size() < 4) {
            return matches;
        }

        List<Long> sortedPrices = matches.stream()
                .map(AuctionPriceReferenceMatch::getWinnerPrice)
                .sorted()
                .toList();

        double q1 = percentile(sortedPrices, 0.25d);
        double q3 = percentile(sortedPrices, 0.75d);
        double iqr = q3 - q1;
        double lowerBound = q1 - (iqr * outlierMultiplier);
        double upperBound = q3 + (iqr * outlierMultiplier);

        return matches.stream()
                .filter(match -> match.getWinnerPrice() != null)
                .filter(match -> match.getWinnerPrice() >= lowerBound && match.getWinnerPrice() <= upperBound)
                .sorted(Comparator.comparing(AuctionPriceReferenceMatch::getSimilarity).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private double weightedAverage(List<AuctionPriceReferenceMatch> matches) {
        double weightedSum = 0d;
        double weightTotal = 0d;

        for (AuctionPriceReferenceMatch match : matches) {
            double weight = Math.max(match.getSimilarity() != null ? match.getSimilarity() : 0d, minWeight);
            weightedSum += match.getWinnerPrice() * weight;
            weightTotal += weight;
        }

        return weightTotal == 0d ? 0d : weightedSum / weightTotal;
    }

    private double averageSimilarity(List<AuctionPriceReferenceMatch> matches) {
        return matches.stream()
                .map(AuctionPriceReferenceMatch::getSimilarity)
                .filter(similarity -> similarity != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0d);
    }

    private double percentile(List<Long> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0d;
        }

        double index = percentile * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }

        double ratio = index - lowerIndex;
        return sortedValues.get(lowerIndex) + ((sortedValues.get(upperIndex) - sortedValues.get(lowerIndex)) * ratio);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100d) / 100d;
    }

    private boolean isTimeout(Throwable throwable) {
        return hasCause(throwable, TimeoutException.class)
                || hasCause(throwable, SocketTimeoutException.class)
                || hasCause(throwable, WebClientRequestException.class);
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> targetType) {
        Throwable current = throwable;

        while (current != null) {
            if (targetType.isInstance(current)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
