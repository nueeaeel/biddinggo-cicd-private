package com.biddingmate.biddinggo.inspection.dto;

import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "관리자용 검수 목록 조회 응답 DTO")
public class AdminInspectionListResponse {
    @Schema(description = "검수 ID", example = "12")
    private Long inspectionId;
    @Schema(description = "상품 ID", example = "10")
    private Long itemId;
    @Schema(description = "상품명", example = "Galaxy S25")
    private String name;
    @Schema(description = "판매자 닉네임", example = "선우짱123")
    private String sellerNickname;
    @Schema(description = "검수 상태", example = "PENDING")
    private ItemInspectionStatus status;
    @Schema(description = "상품 상태", example = "최상")
    private String quality;
    @Schema(description = "신청 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;
}