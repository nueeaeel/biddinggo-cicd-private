package com.biddingmate.biddinggo.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateWishlistRequest {
    private Long auctionId;
}