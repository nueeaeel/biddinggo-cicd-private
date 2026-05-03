package com.biddingmate.biddinggo.member.dto;

import com.biddingmate.biddinggo.auction.model.YesNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSalesAuctionResponse {

    // 상품명
    private String itemName;

    // 현재 최고가
    private Long currentPrice;

    // 시작가
    private Long startPrice;

    // 입찰 수
    private Integer bidCount;

    // 종료 날짜
    private LocalDateTime endDate;

    // 경매 타입 (NORMAL, TIME_DEAL, INSPECTION)
    private String auctionType;

    // 검수 여부
    private YesNo inspectionYn;

    // 대표 이미지 URL
    private String imageUrl;

    // 판매 상태 (ONGOING, SUCCESS, FAILED)
    private String saleStatus;

    // 경매 ID
    private Long auctionId;
}
