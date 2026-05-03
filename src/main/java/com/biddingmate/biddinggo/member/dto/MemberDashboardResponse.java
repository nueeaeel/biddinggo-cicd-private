package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDashboardResponse {

    // 대시보드에 노출될 회원 정보
    private String nickname;
    private String grade;
    private Long point;
    private String imageUrl;

    // 진행 중인 구매 현황
    private List<MemberWonItemResponse> purchaseItems;

    // 진행 중인 판매 현황
    private List<MemberSalesItemResponse> salesItems;

    // 입찰 내역
    private List<MemberBiddingItemResponse> biddingItems;
}