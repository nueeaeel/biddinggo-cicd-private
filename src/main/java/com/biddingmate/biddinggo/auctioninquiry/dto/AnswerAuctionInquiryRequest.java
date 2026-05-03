package com.biddingmate.biddinggo.auctioninquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAuctionInquiryRequest {

    @NotBlank(message = "답변 내용은 필수입니다.")
    private String answer;
}