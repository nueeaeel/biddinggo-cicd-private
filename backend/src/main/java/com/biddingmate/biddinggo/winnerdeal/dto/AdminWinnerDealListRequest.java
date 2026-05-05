package com.biddingmate.biddinggo.winnerdeal.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "관리자 거래 내역 조회 요청")
public class AdminWinnerDealListRequest extends BasePageRequest {
    @Schema(description = "거래 상태", example = "CONFIRMED", nullable = true)
    private WinnerDealStatus status;

    @Schema(description = "거래 번호", example = "WD_20260409_A1B2C3", nullable = true)
    private String dealNumber;
}
