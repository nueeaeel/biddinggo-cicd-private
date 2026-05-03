package com.biddingmate.biddinggo.inspection.dto;

import com.biddingmate.biddinggo.item.model.AuctionItemStatus;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "검수물품 목록 조회 응답 DTO")
public class InspectionListResponse {
    @Schema(description = "검수 ID", example = "12")
    private Long inspectionId;

    @Schema(description = "상품 ID", example = "10")
    private Long itemId;

    @Schema(description = "브랜드명", example = "Nike")
    private String brand;

    @Schema(description = "상품명", example = "Air Jordan 1 High")
    private String name;

    @Schema(description = "상품 상태", example = "최상")
    private String quality;

    @Schema(description = "검수 상태", example = "PENDING")
    private ItemInspectionStatus inspectionStatus;

    @Schema(description = "경매 상품 상태", example = "PENDING")
    private AuctionItemStatus auctionItemStatus;

    @Schema(description = "대표 이미지 URL", nullable = true)
    private String representativeImageUrl;

    @Schema(description = "택배사", nullable = true)
    private String carrier;

    @Schema(description = "송장 번호", nullable = true)
    private String trackingNumber;

    @Schema(description = "검수 등록일시")
    private LocalDateTime createdAt;
}
