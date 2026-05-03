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
public class MemberAuctionInquiryResponse {

    private Long id;

    // 내가 구매하려는 글에 작성한 것인지, 판매하려는 글에 작성된 것인지 구분하기 위한 타입
    private String inquiryType;

    // 답변 상태
    private String answerStatus;

    // 문의 날짜
    private LocalDateTime createdAt;

    // 문의 제목
    private String title;

    // 문의 내용
    private String content;

    // 답변 내용
    private String answer;

    // 답변 날짜
    private LocalDateTime answeredAt;

    // 경매 id
    private Long auctionId;
}
