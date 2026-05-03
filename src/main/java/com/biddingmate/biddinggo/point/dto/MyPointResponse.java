package com.biddingmate.biddinggo.point.dto;

import com.biddingmate.biddinggo.common.response.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 포인트 조회 응답 DTO")
public class MyPointResponse {
    @Schema(description = "현재 보유 포인트", example = "15000")
    private long currentPoint;
    @Schema(description = "포인트 내역 목록")
    private PageResponse<PointHistoryDto> histroies;
}