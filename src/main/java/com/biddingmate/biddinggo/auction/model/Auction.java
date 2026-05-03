package com.biddingmate.biddinggo.auction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auction {
    private Long id;
    private Long itemId;
    private Long winnerId;
    private Long sellerId;
    private AuctionType type;
    private YesNo inspectionYn;
    private YesNo extensionYn;
    private AuctionStatus status;
    private Long startPrice;
    private Integer bidUnit;
    private Long vickreyPrice;
    private Integer bidCount;
    private Long buyNowPrice;
    private Integer wishCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancelDate;
    private Long winnerPrice;
    private LocalDateTime createdAt;
}
