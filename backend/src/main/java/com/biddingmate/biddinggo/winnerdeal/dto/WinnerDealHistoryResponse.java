package com.biddingmate.biddinggo.winnerdeal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "낙찰 거래 내역 응답")
public class WinnerDealHistoryResponse {
    @Schema(description = "낙찰 거래 ID", example = "101")
    private Long winnerDealId;

    @Schema(description = "거래 번호", example = "WD_20260409_A1B2C3")
    private String dealNumber;

    @Schema(description = "경매 ID", example = "201")
    private Long auctionId;

    @Schema(description = "상품 ID", example = "301")
    private Long itemId;

    @Schema(description = "상품명", example = "갤럭시 S23 Ultra")
    private String itemName;

    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/item/1.jpg")
    private String imageUrl;

    @Schema(description = "화면 표시용 거래 상태", example = "배송 중")
    private String status;

    @Schema(description = "거래 금액", example = "950000")
    private Long winnerPrice;

    @Schema(description = "구매 확정일", example = "2026-03-05T18:30:00")
    private LocalDateTime confirmedAt;

    @Schema(description = "거래 일시", example = "2026-03-03T14:00:00")
    private LocalDateTime createdAt;
}
