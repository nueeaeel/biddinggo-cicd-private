package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberBiddingItemResponse {

    // 입찰한 경매 ID
    private Long auctionId;

    // 상품 대표 이미지 URL
    private String imageUrl;

    // 상품명
    private String itemName;

    // 현재가
    private Long currentPrice;

    // 내 입찰가
    private Long myBidPrice;

    // 경매 종료 일시
    private LocalDateTime endDate;

    // 경매 상태
    private String status;
}
