package com.biddingmate.biddinggo.notification.service;

import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationSseService {
    // SseEmitter 생성 -> emitter 저장소에 저장 -> 연결 완료/타임아웃/에러 시 제거 -> 최초 dummy event 전송
    SseEmitter subscribe(Long memberId);

    // 해당 사용자 emitter 찾기 -> 있으면 이벤트 전송 -> 전송 실패시 emitter 제거
    void send(Long receiverId, NotificationResponse response);
}
