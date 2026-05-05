package com.biddingmate.biddinggo.notification.dto;

import com.biddingmate.biddinggo.notification.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "알림 조회 응답 DTO")
public class NotificationResponse {
    @Schema(description = "조회된 알림")
    private Long id;
    private NotificationType type;
    private String content;
    private String url;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}