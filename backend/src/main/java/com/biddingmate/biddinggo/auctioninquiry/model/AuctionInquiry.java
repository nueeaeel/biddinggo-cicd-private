package com.biddingmate.biddinggo.auctioninquiry.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionInquiry {

    private Long id;
    private Long auctionId;
    private Long writerId;
    private Long answererId;
    private String title;
    private String content;
    private String answer;
    private boolean secretYn;

    private LocalDateTime answeredAt;

    private AuctionInquiryStatus status;
    private LocalDateTime createdAt;
}