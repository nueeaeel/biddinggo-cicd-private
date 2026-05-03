package com.biddingmate.biddinggo.directinquiry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectInquiry {
    private Long id;
    private Long writerId;
    private Long adminId;
    private String category;
    private String content;
    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
