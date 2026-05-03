package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPurchaseItemResponse {

    // 상품명
    private String itemName;

    // 판매자 이름
    private String sellerName;

    // 구매 금액 (낙찰가)
    private Long price;

    // 거래 완료 날짜
    private LocalDateTime completedAt;

    // 배송 상태
    private String deliveryStatus;
}
