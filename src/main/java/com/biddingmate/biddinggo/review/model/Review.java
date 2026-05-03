package com.biddingmate.biddinggo.review.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long dealId;      // 거래 ID
    private Long writerId;    // 작성자 ID
    private Long targetId;    // 리뷰 대상자 ID
    private Integer rating;   // 별점
    private String content;   // 리뷰 내용
    private LocalDateTime createdAt;
}
