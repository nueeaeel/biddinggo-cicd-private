package com.biddingmate.biddinggo.auction.prediction.service;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceSyncCommand;

/**
 * 낙찰 완료 데이터를 예측용 과거 reference 저장소로 동기화하는 서비스 경계.
 */
public interface AuctionPriceReferenceSyncService {
    /**
     * 조합된 낙찰 reference 동기화 커맨드를 외부 저장소 연동 계층으로 전달한다.
     */
    void sync(AuctionPriceReferenceSyncCommand command);
}
