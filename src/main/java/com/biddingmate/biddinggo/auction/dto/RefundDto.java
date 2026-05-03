package com.biddingmate.biddinggo.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefundDto {
    private Long bidderId;
    private Long amount;
}
