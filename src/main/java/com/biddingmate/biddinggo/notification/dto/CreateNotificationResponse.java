package com.biddingmate.biddinggo.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "알림 등록 응답 DTO")
public class CreateNotificationResponse {
    @Schema(description = "생성된 알림 ID")
    private Long id;
}
