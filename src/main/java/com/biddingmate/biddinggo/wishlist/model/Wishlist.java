package com.biddingmate.biddinggo.wishlist.model;

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
public class Wishlist {
    private Long id;
    private Long memberId;
    private Long auctionId;
    private LocalDateTime createdAt;
}
