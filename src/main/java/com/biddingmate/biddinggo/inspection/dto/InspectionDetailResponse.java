package com.biddingmate.biddinggo.inspection.dto;

import com.biddingmate.biddinggo.inspection.model.InspectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "검수 아이템 상세 조회 응답 DTO")
public class InspectionDetailResponse {
    @Schema(description = "검수 ID", example = "12")
    private Long inspectionId;

    @Schema(description = "검수 상태", example = "PENDING")
    private InspectionStatus status;

    @Schema(description = "검수 수령 일시", nullable = true)
    private LocalDateTime receivedAt;

    @Schema(description = "검수 완료 일시", nullable = true)
    private LocalDateTime completedAt;

    @Schema(description = "검수 실패 사유", nullable = true)
    private String failureReason;

    @Schema(description = "택배사", nullable = true)
    private String carrier;

    @Schema(description = "송장 번호", nullable = true)
    private String trackingNumber;

    @Schema(description = "검수 생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "상품 정보")
    private Item item;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "검수 대상 상품 정보")
    public static class Item {
        @Schema(description = "상품 ID", example = "10")
        private Long itemId;

        @Schema(description = "판매자 ID", example = "1")
        private Long sellerId;

        @Schema(description = "브랜드명", example = "Nike")
        private String brand;

        @Schema(description = "상품명", example = "Air Jordan 1 High")
        private String name;

        @Schema(description = "상품 상태", example = "최상")
        private String quality;

        @Schema(description = "상품 설명", example = "실착 2회, 박스 포함")
        private String description;

        @Schema(description = "카테고리 정보")
        private Category category;

        @Schema(description = "상품 이미지 목록")
        private List<Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "카테고리 정보")
    public static class Category {
        @Schema(description = "카테고리 ID", example = "100")
        private Long id;

        @Schema(description = "카테고리명", example = "하이탑")
        private String name;

        @Schema(description = "카테고리 level", example = "3")
        private Integer level;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "상품 이미지 정보")
    public static class Image {
        @Schema(description = "이미지 URL")
        private String url;

        @Schema(description = "노출 순서", example = "1")
        private Integer displayOrder;
    }
}
