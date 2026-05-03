package com.biddingmate.biddinggo.inspection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "상품 검수 등록 응답 DTO")
public class CreateInspectionResponse {
    @Schema(description = "생성된 검수 ID", example = "12")
    private Long inspectionId;
}
