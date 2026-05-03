package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbeddingSyncCommand;

/**
 * 조회용 임베딩 동기화 작업을 실제 저장소 연동으로 넘기는 서비스 경계.
 * 현재는 로깅 구현체를 사용하고, 이후 Supabase 연동 구현체로 교체될 예정이다.
 */
public interface AuctionQueryEmbeddingSyncService {
    /**
     * 조합된 임베딩 동기화 커맨드를 외부 저장소 연동 계층으로 전달한다.
     */
    void sync(AuctionQueryEmbeddingSyncCommand command);
}
