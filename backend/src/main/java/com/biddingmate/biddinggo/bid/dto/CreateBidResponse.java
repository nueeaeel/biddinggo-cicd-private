package com.biddingmate.biddinggo.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "경매 입찰 등록 응답 DTO")
public class CreateBidResponse {
    @Schema(description = "생성된 입찰 ID", example = "5")
    private Long bidId;
}