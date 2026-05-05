package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.auction.model.AuctionType;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(description = "경매 상세 조회 응답 DTO")
public class AuctionDetailResponse {
    @Schema(description = "경매 ID", example = "1")
    private Long auctionId;

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @Schema(description = "판매자 닉네임", example = "bidding_seller")
    private String sellerNickname;

    @Schema(description = "판매자 프로필 이미지 URL")
    private String sellerImageUrl;

    @Schema(description = "판매자 등급", example = "GOLD")
    private String sellerGrade;

    @Schema(description = "판매자 가입일시")
    private LocalDateTime sellerCreatedAt;

    @Schema(description = "판매자 평균 리뷰 평점", example = "4.8")
    private Double sellerRating;

    @Schema(description = "판매자 리뷰 수", example = "12")
    private Long sellerReviewCount;

    @Schema(description = "경매 상태", example = "ON_GOING")
    private AuctionStatus status;

    @Schema(description = "경매 타입", example = "NORMAL")
    private AuctionType type;

    @Schema(description = "검수 여부", example = "NO")
    private YesNo inspectionYn;

    @Schema(description = "연장 경매 여부", example = "NO")
    private YesNo extensionYn;

    @Schema(description = "시작가", example = "100000")
    private Long startPrice;

    @Schema(description = "입찰 단위", example = "1000")
    private Integer bidUnit;

    @Schema(description = "비크리 가격", example = "150000", nullable = true)
    private Long vickreyPrice;

    @Schema(description = "즉시 구매가", example = "180000", nullable = true)
    private Long buyNowPrice;

    @Schema(description = "입찰 수", example = "0")
    private Integer bidCount;

    @Schema(description = "찜 수", example = "0")
    private Integer wishCount;

    @Schema(description = "경매 시작일시")
    private LocalDateTime startDate;

    @Schema(description = "경매 종료일시")
    private LocalDateTime endDate;

    @Schema(description = "경매 취소일시", nullable = true)
    private LocalDateTime cancelDate;

    @Schema(description = "경매 생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "상품 정보")
    private Item item;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "경매 예측가 정보", nullable = true)
    private AuctionPricePredictionResponse pricePrediction;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "경매 상품 정보")
    public static class Item {
        @Schema(description = "상품 ID", example = "10")
        private Long itemId;

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
