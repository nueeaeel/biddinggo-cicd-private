package com.biddingmate.biddinggo.inspection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "관리자용 검수 처리 요청 DTO")
public class InspectionProcessRequest {
    @Schema(description = "검수 승인 여부", example = "true")
    @NotNull(message = "검수 승인 여부는 필수입니다.")
    private Boolean approved;
    @Schema(description = "검수 품질", example = "최상", nullable = true)
    private String quality;
    @Schema(description = "검수 실패 사유", example = "오염/스크래치", nullable = true)
    private String failureReason;
}
