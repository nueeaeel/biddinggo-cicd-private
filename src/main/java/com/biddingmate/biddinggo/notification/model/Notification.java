package com.biddingmate.biddinggo.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    private Long id;
    private Long receiverId;
    private NotificationType type;
    private String content;
    private String url;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
