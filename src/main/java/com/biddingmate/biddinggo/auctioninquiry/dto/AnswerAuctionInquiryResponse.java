package com.biddingmate.biddinggo.auctioninquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAuctionInquiryResponse {

    private Long id;
    private String answer;      

    private LocalDateTime answeredAt;
}