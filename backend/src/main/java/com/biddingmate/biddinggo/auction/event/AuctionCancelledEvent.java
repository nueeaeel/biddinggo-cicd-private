package com.biddingmate.biddinggo.auction.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AuctionCancelledEvent {
    private List<Long> auctionIds;
}
