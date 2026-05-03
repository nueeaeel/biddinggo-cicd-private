package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.prediction.client.AuctionEmbeddingClient;
import com.biddingmate.biddinggo.auction.prediction.client.AuctionPredictionSupabaseClient;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingResult;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReference;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceSyncCommand;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 낙찰 완료 데이터를 벡터 reference로 변환해 Supabase에 저장하는 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseAuctionPriceReferenceSyncService implements AuctionPriceReferenceSyncService {
    private final AuctionEmbeddingClient auctionEmbeddingClient;
    private final AuctionPredictionSupabaseClient auctionPredictionSupabaseClient;

    @Override
    public void sync(AuctionPriceReferenceSyncCommand command) {
        if (command == null || command.getAuctionId() == null || command.getItemId() == null || command.getCategoryId() == null) {
            log.warn("Skip auction price reference sync because command is incomplete.");
            return;
        }

        if (command.getWinnerPrice() == null || command.getWinnerPrice() <= 0) {
            log.warn("Skip auction price reference sync because winner price is invalid. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
            return;
        }

        if (command.getConditionScore() == null) {
            log.warn("Skip auction price reference sync because condition score is unavailable. auctionId={}, itemId={}, quality={}", command.getAuctionId(), command.getItemId(), command.getQuality());
            return;
        }

        if (!StringUtils.hasText(command.getEmbeddingText())) {
            log.warn("Skip auction price reference sync because embedding text is blank. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
            return;
        }

        if (!auctionPredictionSupabaseClient.isEnabled()) {
            log.info("Skip auction price reference sync because Supabase integration is not enabled. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
            return;
        }

        try {
            AuctionQueryEmbedding queryEmbedding = auctionPredictionSupabaseClient.findAuctionQueryEmbedding(command.getAuctionId());

            List<Double> embedding = queryEmbedding != null ? queryEmbedding.getEmbedding() : null;
            String embeddingModel = queryEmbedding != null ? queryEmbedding.getEmbeddingModel() : null;
            Integer embeddingDimension = queryEmbedding != null ? queryEmbedding.getEmbeddingDimension() : null;
            String embeddingText = queryEmbedding != null && StringUtils.hasText(queryEmbedding.getEmbeddingText())
                    ? queryEmbedding.getEmbeddingText()
                    : command.getEmbeddingText();

            if (embedding == null || embedding.isEmpty()) {
                if (!auctionEmbeddingClient.isEnabled()) {
                    log.info("Skip auction price reference sync because no reusable query embedding exists and embedding integration is disabled. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
                    return;
                }

                AuctionEmbeddingResult embeddingResult = auctionEmbeddingClient.createEmbedding(command.getEmbeddingText());
                embedding = embeddingResult.getEmbedding();
                embeddingModel = embeddingResult.getModel();
                embeddingDimension = embeddingResult.getDimension();
                embeddingText = command.getEmbeddingText();
            }

            if (embeddingDimension == null) {
                embeddingDimension = embedding.size();
            }

            AuctionPriceReference auctionPriceReference = AuctionPriceReference.builder()
                    .auctionId(command.getAuctionId())
                    .itemId(command.getItemId())
                    .categoryId(command.getCategoryId())
                    .winnerPrice(command.getWinnerPrice())
                    .quality(command.getQuality())
                    .conditionScore(command.getConditionScore())
                    .embedding(embedding)
                    .embeddingModel(embeddingModel)
                    .embeddingDimension(embeddingDimension)
                    .embeddingText(embeddingText)
                    .completedAt(command.getCompletedAt() != null ? command.getCompletedAt() : LocalDateTime.now())
                    .build();

            auctionPredictionSupabaseClient.upsertAuctionPriceReference(auctionPriceReference);

            log.info(
                    "Synced auction price reference to Supabase. auctionId={}, itemId={}, categoryId={}, winnerPrice={}",
                    command.getAuctionId(),
                    command.getItemId(),
                    command.getCategoryId(),
                    command.getWinnerPrice()
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to sync auction price reference to Supabase. auctionId={}, itemId={}, categoryId={}",
                    command.getAuctionId(),
                    command.getItemId(),
                    command.getCategoryId(),
                    exception
            );
        }
    }
}
