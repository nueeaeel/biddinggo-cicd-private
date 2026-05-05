package com.biddingmate.biddinggo.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "가상계좌 발급 요청 DTO")
public class CreateVirtualAccountRequest {
    // 토스 요청 필드
    @Schema(description = "주문 ID", example = "ORD-20260405-0001")
    private String orderId;
    @Schema(description = "주문명", example = "포인트 충전")
    private String orderName;
    @Schema(description = "결제 금액", example = "10000")
    private Long amount;
    @Schema(description = "고객명", example = "홍길동")
    private String customerName;
    @Schema(description = "은행 코드", example = "20")
    private String bank;
}

