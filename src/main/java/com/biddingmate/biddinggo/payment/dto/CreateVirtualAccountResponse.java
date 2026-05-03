package com.biddingmate.biddinggo.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "가상계좌 발급 응답 DTO")
public class CreateVirtualAccountResponse {
    @Schema(description = "결제 상태", example = "WAITING_FOR_DEPOSIT")
    private String status;
    @Schema(description = "주문 ID", example = "ORD-20260405-0001")
    private String orderId;
    @Schema(description = "결제 금액", example = "10000")
    private Long amount;
    @Schema(description = "은행 코드", example = "KB")
    private String bankCode;
    @Schema(description = "가상계좌 번호", example = "12345678901234")
    private String bankAccount;
    @Schema(description = "예금주", example = "홍길동")
    private String accountHolderName;
    @Schema(description = "입금 기한", example = "2026-04-06T23:59:59")
    private LocalDateTime dueDate;
}
