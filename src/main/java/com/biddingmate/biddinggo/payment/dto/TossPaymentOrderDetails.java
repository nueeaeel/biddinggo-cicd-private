package com.biddingmate.biddinggo.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@Schema(description = "토스 결제 주문 상세 DTO")
public class TossPaymentOrderDetails {
    @Schema(description = "결제 고유 키", example = "pay_20260405_0001")
    private String paymentKey;
    @Schema(description = "주문 ID", example = "ORD-20260405-0001")
    private String orderId;
    @Schema(description = "결제 상태", example = "DONE")
    private String status;
    @Schema(description = "총 결제 금액", example = "10000")
    private Long totalAmount;
    @Schema(description = "결제 수단", example = "VIRTUAL_ACCOUNT")
    private String method;
    @Schema(description = "승인 시각", example = "2026-04-05T12:35:00")
    private String approvedAt;
    @Schema(description = "요청 시각", example = "2026-04-05T12:30:00")
    private String requestedAt;

    // 가상계좌의 경우, 입금 기한이나 계좌번호를 다시 확인해야 할 수도 있으므로 포함
    @Schema(description = "가상계좌 정보")
    private VirtualAccount virtualAccount;
    @Getter
    @NoArgsConstructor
    @Schema(description = "토스 가상계좌 정보")
    public static class VirtualAccount {
        @Schema(description = "가상계좌 번호", example = "12345678901234")
        private String accountNumber;
        @Schema(description = "은행 코드", example = "KB")
        private String bankCode;
        @Schema(description = "예금주", example = "홍길동")
        private String customerName;
        @Schema(description = "입금 기한", example = "2026-04-06T23:59:59")
        private String dueDate;
    }
}

