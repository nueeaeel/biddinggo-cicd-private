package com.biddingmate.biddinggo.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "결제 정보 DTO")
public class PaymentInfo {
    @Schema(description = "결제 ID", example = "1")
    private Long paymentId;
    @Schema(description = "회원 ID", example = "10")
    private Long memberId;
    @Schema(description = "결제 금액", example = "10000")
    private Long amount;
}