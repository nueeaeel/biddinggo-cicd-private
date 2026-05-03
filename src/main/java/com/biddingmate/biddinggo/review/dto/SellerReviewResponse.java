package com.biddingmate.biddinggo.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerReviewResponse {

    // 리뷰 작성자(구매자)의 닉네임
    private String buyerNickname;

    // 별점
    private Double rating;

    // 리뷰 내용
    private String reviewText;

    // 리뷰 작성일
    private LocalDateTime reviewDate;
}
