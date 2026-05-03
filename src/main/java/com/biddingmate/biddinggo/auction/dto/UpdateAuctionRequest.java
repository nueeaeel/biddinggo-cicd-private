package com.biddingmate.biddinggo.auction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "경매 수정 요청 DTO")
public class UpdateAuctionRequest {
    @Schema(description = "시작가", example = "100000")
    @PositiveOrZero(message = "시작가는 0 이상이어야 합니다.")
    private Long startPrice;

    @Schema(description = "입찰 단위", example = "1000")
    @Positive(message = "입찰 단위는 1 이상이어야 합니다.")
    private Integer bidUnit;

    @Schema(description = "즉시 구매가", example = "180000", nullable = true)
    @PositiveOrZero(message = "즉시 구매가는 0 이상이어야 합니다.")
    private Long buyNowPrice;

    @Schema(description = "경매 시작일시", example = "2026-03-15T10:00:00")
    @NotNull(message = "경매 시작일시는 필수입니다.")
    private LocalDateTime startDate;

    @Schema(description = "경매 종료일시", example = "2026-03-16T10:00:00")
    @NotNull(message = "경매 종료일시는 필수입니다.")
    private LocalDateTime endDate;
}
