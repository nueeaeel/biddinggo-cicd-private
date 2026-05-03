package com.biddingmate.biddinggo.auction.prediction.client;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReference;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceMatch;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbedding;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbeddingMatch;

import java.util.List;

/**
 * Supabase 벡터 저장소에 query embedding / price reference를 적재하는 연동 경계.
 */
public interface AuctionPredictionSupabaseClient {
    /**
     * 현재 환경에서 Supabase 연동이 가능한지 확인한다.
     */
    boolean isEnabled();

    /**
     * 경매 조회 대상 임베딩을 Supabase에 upsert 한다.
     */
    void upsertAuctionQueryEmbedding(AuctionQueryEmbedding auctionQueryEmbedding);

    /**
     * auction_id를 기준으로 저장된 query embedding을 조회한다.
     */
    AuctionQueryEmbedding findAuctionQueryEmbedding(Long auctionId);

    /**
     * 과거 낙찰 reference를 Supabase에 upsert 한다.
     */
    void upsertAuctionPriceReference(AuctionPriceReference auctionPriceReference);

    /**
     * 저장된 query embedding을 기준으로 유사 낙찰 reference를 검색한다.
     */
    List<AuctionPriceReferenceMatch> matchAuctionPriceReferences(
            List<Double> queryEmbedding,
            Long categoryId,
            Double minConditionScore,
            Double maxConditionScore,
            Integer matchCount,
            Double minSimilarity,
            Long excludeAuctionId
    );

    /**
     * 검색어 임베딩을 기준으로 유사한 경매 query embedding 후보를 검색한다.
     * 최종 정렬/상태 필터는 애플리케이션의 MariaDB 조회 단계에서 다시 적용한다.
     */
    List<AuctionQueryEmbeddingMatch> matchAuctionQueryEmbeddings(
            List<Double> queryEmbedding,
            Integer matchCount,
            Double minSimilarity
    );
}
