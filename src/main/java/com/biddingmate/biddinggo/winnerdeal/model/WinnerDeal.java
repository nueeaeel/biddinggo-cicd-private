package com.biddingmate.biddinggo.winnerdeal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinnerDeal {
    private Long id;
    private Long auctionId;
    private Long winnerId;
    private Long sellerId;
    private String dealNumber;
    private Long winnerPrice;
    private WinnerDealStatus status;
    private LocalDateTime confirmedAt;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String tel;
    private String recipient;
    private String carrier;
    private String trackingNumber;
    private LocalDateTime createdAt;
}
