package com.biddingmate.biddinggo.bid.dto;

import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "입찰 리스트 조회 응답 DTO")
public class BidListResponse {
    private Long id;
    private Long auctionId;
    private AuctionStatus status;
    private Long startPrice;
    private Long vickreyPrice;
    private Integer bidCount;
    private Integer wishCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancel_date;
    private String name;
    private String representativeImageUrl;
    private Long amount;
    private LocalDateTime createdAt;
}
