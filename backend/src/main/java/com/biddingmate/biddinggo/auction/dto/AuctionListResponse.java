package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.auction.model.AuctionType;
import com.biddingmate.biddinggo.auction.model.YesNo;
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
@Schema(description = "경매 목록 조회 응답 DTO")
public class AuctionListResponse {
    @Schema(description = "경매 ID", example = "1")
    private Long auctionId;

    @Schema(description = "상품 ID", example = "10")
    private Long itemId;

    @Schema(description = "브랜드명", example = "Nike")
    private String brand;

    @Schema(description = "상품명", example = "Air Jordan 1 High")
    private String name;

    @Schema(description = "경매 상태", example = "ON_GOING")
    private AuctionStatus status;

    @Schema(description = "경매 타입", example = "TIME_DEAL")
    private AuctionType type;

    @Schema(description = "검수 여부", example = "NO")
    private YesNo inspectionYn;

    @Schema(description = "연장 경매 여부", example = "NO")
    private YesNo extensionYn;

    @Schema(description = "시작가", example = "100000")
    private Long startPrice;

    @Schema(description = "즉시 구매가", example = "180000", nullable = true)
    private Long buyNowPrice;

    @Schema(description = "차순위 입찰가", example = "150000", nullable = true)
    private Long vickreyPrice;

    @Schema(description = "입찰 수", example = "0")
    private Integer bidCount;

    @Schema(description = "관심 수", example = "0")
    private Integer wishCount;

    @Schema(description = "경매 종료일시")
    private LocalDateTime endDate;

    @Schema(description = "대표 이미지 URL", nullable = true)
    private String representativeImageUrl;

    @Schema(description = "경매 생성일시")
    private LocalDateTime createdAt;
}
