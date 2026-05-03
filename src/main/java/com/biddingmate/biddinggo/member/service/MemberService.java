package com.biddingmate.biddinggo.member.service;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.dto.MemberDashboardResponse;
import com.biddingmate.biddinggo.member.dto.MemberListView;
import com.biddingmate.biddinggo.member.dto.MemberListViewRequest;
import com.biddingmate.biddinggo.member.dto.MemberProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberProfileUpdateRequest;
import com.biddingmate.biddinggo.member.dto.MemberPurchaseItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResultResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSellerProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResponse;
import com.biddingmate.biddinggo.member.dto.UpdateMemberStatusRequest;
import jakarta.validation.Valid;

public interface MemberService {
    MemberDashboardResponse getMyDashboard(Long id);

    MemberProfileResponse getMyProfile(Long memberId);

    MemberProfileResponse updateMyProfile(Long memberId, MemberProfileUpdateRequest request);

    void deleteMyAccount(Long memberId);

    PageResponse<MemberSalesItemResponse> getMySales(Long memberId, BasePageRequest pageRequest);
  
    PageResponse<MemberPurchaseItemResponse> getMyPurchases(Long memberId, BasePageRequest pageRequest);

    long getCurrentPoint(Long memberId);
    void addPoint(Long memberId, Long amount);
    void deductPoint(Long memberId, Long amount);

    MemberSalesAuctionResultResponse getMySalesAuctions(Long memberId, String type, BasePageRequest pageRequest);

    PageResponse<MemberListView> findAllMemberWithFilter(@Valid MemberListViewRequest request);
    void updateMemberStatus(Long memberId, @Valid UpdateMemberStatusRequest request);
    MemberSellerProfileResponse getSellerProfile(Long sellerId);

    // 사용자 VIP 등급 재산정
    void recalculateMemberGrade(Long memberId);
}
