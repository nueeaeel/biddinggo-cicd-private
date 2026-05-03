package com.biddingmate.biddinggo.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewResponse {
    private Long id;           // 생성된 리뷰의 PK
    private Long auctionId;    // 리뷰가 달린 경매 ID
    private Integer rating;    // 등록된 별점
    private String content;    // 등록된 내용
    private LocalDateTime createdAt; // 등록 시간
}