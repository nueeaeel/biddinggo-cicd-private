package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSalesAuctionSummaryResponse {

    // 전체 경매 개수
    private Long totalCount;

    // 진행중 개수
    private Long ongoingCount;

    // 낙찰 개수
    private Long successCount;

    // 유찰 개수
    private Long failedCount;
}
