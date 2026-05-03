package com.biddingmate.biddinggo.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "포인트 내역 DTO")
public class PointHistoryDto {
    @Schema(description = "포인트 내역 ID", example = "1")
    private Long id;
    @Schema(description = "내역 유형", example = "CHARGE")
    private String type;
    @Schema(description = "포인트 금액", example = "1000")
    private long amount;
    @Schema(description = "생성 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;
}