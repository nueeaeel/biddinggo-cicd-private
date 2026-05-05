package com.biddingmate.biddinggo.notification.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationRequest;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationResponse;
import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import com.biddingmate.biddinggo.notification.dto.UnreadNotificationResponse;
import com.biddingmate.biddinggo.notification.service.NotificationService;
import com.biddingmate.biddinggo.notification.service.NotificationSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    /*
        추후 알림 발생되는 곳에
        모두 알림 등록 프로세스를 구현하면 (notificationService 를 사용하여)
        알림 등록 api 는 삭제 예정
     */

    @PostMapping("/")
    @Operation(summary = "알림 등록", description = "알림을 등록합니다.")
    public ResponseEntity<ApiResponse<CreateNotificationResponse>> createBid(
            @Valid @RequestBody CreateNotificationRequest request
    ) {

        CreateNotificationResponse result = notificationService.createNotification(request);

        return ApiResponse.of(HttpStatus.OK, null, "알림 등록 성공", result);
    }

    @GetMapping("/")
    @Operation(summary = "알림 조회", description = "알림을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotificationsByMemberId(
            BasePageRequest request,
            @AuthenticationPrincipal Member member
    ){

        PageResponse<NotificationResponse> result = notificationService.getNotificationsByMemberId(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "알림 조회 성공", result);
    }

    @PatchMapping("/read-all")
    @Operation(summary = "전체 알림 읽음 처리", description = "모든 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> readAllNotification(
            @AuthenticationPrincipal Member member
    ){
        notificationService.markAllAsRead(member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "전체 알림 읽음 처리", null);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "단건 알림 읽음 처리", description = "단건 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> readAllNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal Member member
    ){
        notificationService.markAsRead(id, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "단건 알림 읽음 처리", null);
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "알림 구독", description = "SSE를 통해 실시간 알림을 구독합니다.")
    public SseEmitter subscribe(
            @AuthenticationPrincipal Member member
    ) {
        return notificationSseService.subscribe(member.getId());
    }

    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 수 조회")
    public ResponseEntity<ApiResponse<UnreadNotificationResponse>> getUnreadCount(
            @AuthenticationPrincipal Member member
    ) {

        UnreadNotificationResponse result = UnreadNotificationResponse.builder()
                .unreadCount(notificationService.countUnread(member.getId()))
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "읽지 않은 알림 수 조회 성공", result);
    }

}
