package com.biddingmate.biddinggo.winnerdeal.dto;

import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "관리자 거래 내역 조회 응답")
public class AdminWinnerDealListResponse {
    @Schema(description = "낙찰 거래 ID", example = "101")
    private Long winnerDealId;

    @Schema(description = "거래 번호", example = "WD_20260409_A1B2C3")
    private String dealNumber;

    @Schema(description = "판매자 닉네임", example = "김셀러")
    private String sellerName;

    @Schema(description = "구매자 닉네임", example = "김구매")
    private String winnerName;

    @Schema(description = "상품명", example = "맥북 프로 M3 14인치")
    private String itemName;

    @Schema(description = "거래 금액", example = "2280000")
    private Long winnerPrice;

    @Schema(description = "거래일", example = "2026-02-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "거래 상태", example = "SHIPPED")
    private WinnerDealStatus status;

    @Schema(description = "검수 경매 여부", example = "YES")
    private YesNo inspectionYn;
}
