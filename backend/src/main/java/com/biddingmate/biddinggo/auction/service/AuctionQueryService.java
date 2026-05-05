package com.biddingmate.biddinggo.auction.service;

import com.biddingmate.biddinggo.auction.dto.AuctionListRequest;
import com.biddingmate.biddinggo.auction.dto.AuctionListResponse;
import com.biddingmate.biddinggo.auction.dto.AuctionDetailResponse;
import com.biddingmate.biddinggo.auction.dto.AuctionSemanticSearchRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;

/**
 * 경매 조회 전용 서비스.
 * 등록/수정 로직과 분리하여 읽기 책임만 담당한다.
 */
public interface AuctionQueryService {
    /**
     * 조건에 맞는 경매 목록을 페이징 조회한다.
     */
    PageResponse<AuctionListResponse> getAuctionList(AuctionListRequest request);

    /**
     * 경매 ID를 기준으로 상세 정보를 조회한다.
     */
    AuctionDetailResponse getAuctionDetail(Long auctionId);

    /**
     * 검색어를 임베딩해 유사한 경매 후보를 찾고,
     * 최종적으로 진행 중 경매만 기존 목록 정렬 기준으로 반환한다.
     */
    PageResponse<AuctionListResponse> searchAuctionsBySemantic(AuctionSemanticSearchRequest request);
}
