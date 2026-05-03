package com.biddingmate.biddinggo.notification.dto;

import com.biddingmate.biddinggo.notification.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "알림 등록 요청 DTO")
public class CreateNotificationRequest {
    @NotNull(message = "수신자 ID는 필수입니다.")
    private Long receiverId;

    @NotNull(message = "알림 유형은 필수입니다.")
    private NotificationType type;

    @NotNull(message = "알림 내용은 필수입니다.")
    private String content;

    private String url;
}