package com.biddingmate.biddinggo.auction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "경매 등록 응답 DTO")
public class CreateAuctionResponse {
    @Schema(description = "생성된 경매 ID", example = "25")
    private Long auctionId;
}
