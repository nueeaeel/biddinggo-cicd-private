package com.biddingmate.biddinggo.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "입찰 마스킹 조회 응답 DTO")
public class BidMaskingResponse {
    @Schema(description = "조회된 마스킹 입찰")
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private String amount;
    private LocalDateTime createdAt;
}
