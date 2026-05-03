package com.biddingmate.biddinggo.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CreateWishlistResponse {
    private Long id;
    private Long memberId;
    private Long auctionId;
    private LocalDateTime createdAt;
}
