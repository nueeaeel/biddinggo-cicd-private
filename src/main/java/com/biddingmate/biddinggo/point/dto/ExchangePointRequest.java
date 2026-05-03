package com.biddingmate.biddinggo.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "포인트 환전 요청 DTO")
public class ExchangePointRequest {
    @Schema(description = "환전 금액", example = "1000")
    @NotNull(message = "환전 금액은 필수입니다.")
    @Min(value = 1, message = "환전 금액은 1 이상이어야 합니다.")
    private Long amount;
}