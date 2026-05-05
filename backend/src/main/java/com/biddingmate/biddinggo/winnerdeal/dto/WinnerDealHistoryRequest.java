package com.biddingmate.biddinggo.winnerdeal.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@SuperBuilder
@Schema(description = "낙찰 거래 내역 조회 요청")
public class WinnerDealHistoryRequest extends BasePageRequest {
    @Schema(description = "거래 상태 필터")
    private WinnerDealStatus status;
}
