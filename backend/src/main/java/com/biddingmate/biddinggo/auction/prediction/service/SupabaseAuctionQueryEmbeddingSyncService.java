package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.prediction.client.AuctionEmbeddingClient;
import com.biddingmate.biddinggo.auction.prediction.client.AuctionPredictionSupabaseClient;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingResult;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbedding;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbeddingSyncCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 4단계에서 사용하는 실제 조회용 임베딩 동기화 서비스.
 * 이벤트로 준비된 텍스트를 임베딩으로 변환한 뒤 Supabase에 저장한다.
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class SupabaseAuctionQueryEmbeddingSyncService implements AuctionQueryEmbeddingSyncService {
    private final AuctionEmbeddingClient auctionEmbeddingClient;
    private final AuctionPredictionSupabaseClient auctionPredictionSupabaseClient;

    @Override
    /**
     * 설정이 준비된 경우에만 임베딩 생성과 Supabase 저장을 수행하고,
     * 실패 시 본 요청 흐름을 깨지 않도록 로그만 남긴다.
     */
    public void sync(AuctionQueryEmbeddingSyncCommand command) {
        if (!StringUtils.hasText(command.getEmbeddingText())) {
            log.warn("Skip auction query embedding sync because embedding text is blank. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
            return;
        }

        if (!auctionEmbeddingClient.isEnabled() || !auctionPredictionSupabaseClient.isEnabled()) {
            log.info("Skip auction query embedding sync because integration is not enabled. auctionId={}, itemId={}", command.getAuctionId(), command.getItemId());
            return;
        }

        try {
            AuctionEmbeddingResult embeddingResult = auctionEmbeddingClient.createEmbedding(command.getEmbeddingText());

            AuctionQueryEmbedding auctionQueryEmbedding = AuctionQueryEmbedding.builder()
                    .auctionId(command.getAuctionId())
                    .itemId(command.getItemId())
                    .categoryId(command.getCategoryId())
                    .embedding(embeddingResult.getEmbedding())
                    .embeddingModel(embeddingResult.getModel())
                    .embeddingDimension(embeddingResult.getDimension())
                    .embeddingText(command.getEmbeddingText())
                    .build();

            auctionPredictionSupabaseClient.upsertAuctionQueryEmbedding(auctionQueryEmbedding);

            log.info(
                    "Synced auction query embedding to Supabase. trigger={}, auctionId={}, itemId={}, categoryId={}, dimension={}",
                    command.getTrigger(),
                    command.getAuctionId(),
                    command.getItemId(),
                    command.getCategoryId(),
                    embeddingResult.getDimension()
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to sync auction query embedding to Supabase. trigger={}, auctionId={}, itemId={}, categoryId={}",
                    command.getTrigger(),
                    command.getAuctionId(),
                    command.getItemId(),
                    command.getCategoryId(),
                    exception
            );
        }
    }
}
