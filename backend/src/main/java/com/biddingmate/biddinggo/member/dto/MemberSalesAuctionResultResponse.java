package com.biddingmate.biddinggo.member.dto;

import com.biddingmate.biddinggo.common.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSalesAuctionResultResponse {

    // 전체 경매, 진행중, 낙찰, 유찰 개수
    private MemberSalesAuctionSummaryResponse summary;

    // 경매 목록
    private PageResponse<MemberSalesAuctionResponse> auctions;
}
