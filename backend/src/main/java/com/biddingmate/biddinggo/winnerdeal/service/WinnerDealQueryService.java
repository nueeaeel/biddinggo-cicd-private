package com.biddingmate.biddinggo.winnerdeal.service;

import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryResponse;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDeal;
import jakarta.validation.Valid;

import java.util.List;

public interface WinnerDealQueryService {
    // 낙찰 완료된 거래를 회원 기준으로 조회
    List<WinnerDeal> findByWinnerId(Long memberId);
    List<WinnerDeal> findBySellerId(Long memberId);

    // 구매내역 조회
    PageResponse<WinnerDealHistoryResponse> findPurchaseHistory(@Valid WinnerDealHistoryRequest request, Long memberId);

    // 판매내역 조회
    PageResponse<WinnerDealHistoryResponse> findSaleHistory(@Valid WinnerDealHistoryRequest request, Long memberId);

    // 거래 내역 상세조회
    WinnerDealDetailResponse findWinnerDealDetail(Long winnerDealId, Long memberId);

    // 관리자 거래 내역 조회
    PageResponse<AdminWinnerDealListResponse> findAdminWinnerDealHistory(@Valid AdminWinnerDealListRequest request);

    // 관리자 거래 내역 상세 조회
    AdminWinnerDealDetailResponse findAdminWinnerDealDetail(Long winnerDealId);
}
