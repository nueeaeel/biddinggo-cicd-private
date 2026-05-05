package com.biddingmate.biddinggo.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "입찰 등록 요청 DTO")
public class CreateBidRequest {
    @NotNull(message = "입찰할 경매 ID는 필수입니다.")
    private Long auctionId;

    @NotNull(message = "입찰 금액은 필수입니다.")
    private Long amount;
}