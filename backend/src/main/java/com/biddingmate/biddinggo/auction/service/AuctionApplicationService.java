package com.biddingmate.biddinggo.auction.service;

import com.biddingmate.biddinggo.auction.dto.CreateAuctionFromInspectionItemRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionRequest;

/**
 * 경매 등록 유스케이스를 조합하는 상위 서비스.
 * 컨트롤러는 이 서비스를 통해 경매 등록을 시작한다.
 */
public interface AuctionApplicationService {
    /**
     * 경매 상품 생성과 경매 생성을 하나의 흐름으로 처리한다.
     */
    Long createAuction(CreateAuctionRequest request, Long memberId);

    /**
     * 검수 완료된 기존 상품을 기준으로 경매를 생성한다.
     */
    Long createAuctionFromInspectionItem(CreateAuctionFromInspectionItemRequest request, Long memberId);
}
