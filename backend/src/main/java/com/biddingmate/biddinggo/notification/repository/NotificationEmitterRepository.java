package com.biddingmate.biddinggo.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationEmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void save(Long memberId, SseEmitter emitter){
        emitters.put(memberId, emitter);
    }

    public SseEmitter get(Long memberId) {
        return emitters.get(memberId);
    }

    public void delete(Long memberId) {
        emitters.remove(memberId);
    }

}
