package com.biddingmate.biddinggo.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private Long amount;
    private LocalDateTime createdAt;
}
