package com.biddingmate.biddinggo.notice.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Notice {

    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
