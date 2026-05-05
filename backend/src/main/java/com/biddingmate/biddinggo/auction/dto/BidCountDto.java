package com.biddingmate.biddinggo.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BidCountDto {
    private Long auctionId;
    private int bidCount;
}
