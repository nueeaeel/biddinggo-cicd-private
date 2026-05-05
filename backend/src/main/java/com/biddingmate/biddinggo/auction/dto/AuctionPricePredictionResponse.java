package com.biddingmate.biddinggo.auction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "경매 예측가 응답 DTO")
public class AuctionPricePredictionResponse {
    @Schema(description = "예측가", example = "157000", nullable = true)
    private Long predictedPrice;

    @Schema(description = "예측에 사용된 유사 낙찰 데이터 수", example = "5", nullable = true)
    private Integer referenceCount;

    @Schema(description = "예측 신뢰도", example = "0.86", nullable = true)
    private Double confidence;

    @Schema(description = "fallback 처리 여부", example = "false")
    private Boolean fallbackUsed;

    @Schema(description = "예측 결과 사유 코드", example = "PREDICTION_AVAILABLE", nullable = true)
    private String reasonCode;
}
