package com.biddingmate.biddinggo.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "토스 가상계좌 입금 웹훅 DTO")
public class TossDepositWebhook {
    @Schema(description = "입금 생성 시각", example = "2026-04-05T12:30:00")
    private String createdAt;
    @Schema(description = "웹훅 검증 시크릿", example = "secret-value")
    private String secret;
    @Schema(description = "결제 상태", example = "DONE")
    private String status;
    @Schema(description = "거래 고유 키", example = "tx-20260405-0001")
    private String transactionKey;
    @Schema(description = "주문 ID", example = "ORD-20260405-0001")
    private String orderId;

    @Override
    public String toString() {
        return "TossDepositWebhookRequest{" +
                "createdAt='" + createdAt + '\'' +
                ", secret='" + secret + '\'' +
                ", status='" + status + '\'' +
                ", transactionKey='" + transactionKey + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}

