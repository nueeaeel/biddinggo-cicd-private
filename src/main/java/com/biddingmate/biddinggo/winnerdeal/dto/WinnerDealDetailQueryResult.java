package com.biddingmate.biddinggo.winnerdeal.dto;

import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WinnerDealDetailQueryResult {
    private Long winnerDealId;
    private String dealNumber;
    private Long auctionId;
    private Long itemId;
    private Long winnerId;
    private Long sellerId;
    private String itemName;
    private String itemImageUrl;
    private Long winnerPrice;
    private WinnerDealStatus status;
    private String sellerName;
    private String winnerName;
    private String recipient;
    private String tel;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String carrier;
    private String trackingNumber;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
}
