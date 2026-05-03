package com.biddingmate.biddinggo.notice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {

    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
