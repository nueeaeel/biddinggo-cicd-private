package com.biddingmate.biddinggo.inspection.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "검수물품 목록 조회 요청 DTO")
public class InspectionListRequest extends BasePageRequest {
    @Schema(description = "검수 상태", example = "PENDING", nullable = true)
    private String status;
}
