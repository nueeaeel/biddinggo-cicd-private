package com.biddingmate.biddinggo.notification.service;

import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import com.biddingmate.biddinggo.notification.repository.NotificationEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationSseServiceImpl implements NotificationSseService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationEmitterRepository emitterRepository;

    @Override
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(memberId));
        emitter.onTimeout(() -> emitterRepository.delete(memberId));
        emitter.onError((e) -> emitterRepository.delete(memberId));

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connect")
                            .data("SSE connected")
            );
        } catch (IOException e) {
            emitterRepository.delete(memberId);
            throw new RuntimeException("SSE 연결 초기화에 실패했습니다.");
        }

        return emitter;
    }

    @Override
    public void send(Long receiverId, NotificationResponse response) {
        SseEmitter emitter = emitterRepository.get(receiverId);

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("notification")
                            .data(response)
            );
        } catch (IOException e) {
            emitterRepository.delete(receiverId);
        }
    }
}
