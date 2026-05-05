package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.auction.model.AuctionType;
import com.biddingmate.biddinggo.auction.model.YesNo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@Schema(description = "검수 완료 상품 경매 등록 요청 DTO")
public class CreateAuctionFromInspectionItemRequest {
    @Schema(description = "경매 등록 대상 상품 ID", example = "10")
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long itemId;

    @Schema(description = "경매 정보")
    @NotNull(message = "경매 정보는 필수입니다.")
    @Valid
    private Auction auction;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "경매 요청 DTO")
    public static class Auction {
        @Schema(description = "경매 타입", example = "NORMAL")
        private AuctionType type;

        @Schema(description = "시작가", example = "100000")
        @PositiveOrZero(message = "시작가는 0 이상이어야 합니다.")
        private Long startPrice;

        @Schema(description = "입찰 단위", example = "1000")
        @Positive(message = "입찰 단위는 1 이상이어야 합니다.")
        private Integer bidUnit;

        @Schema(description = "비크리 가격", example = "150000", nullable = true)
        @PositiveOrZero(message = "비크리 가격은 0 이상이어야 합니다.")
        private Long vickreyPrice;

        @Schema(description = "즉시 구매가", example = "180000", nullable = true)
        @PositiveOrZero(message = "즉시 구매가는 0 이상이어야 합니다.")
        private Long buyNowPrice;

        @Schema(description = "연장 플래그 (NO: 연장경매 허용, YES: 연장경매 미허용)", example = "NO", nullable = true)
        private YesNo extensionYn;

        @Schema(description = "경매 시작일시", example = "2026-03-15T10:00:00")
        @NotNull(message = "경매 시작일시는 필수입니다.")
        private LocalDateTime startDate;

        @Schema(description = "경매 종료일시", example = "2026-03-16T10:00:00")
        @NotNull(message = "경매 종료일시는 필수입니다.")
        private LocalDateTime endDate;
    }
}
