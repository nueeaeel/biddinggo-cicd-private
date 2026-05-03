package com.biddingmate.biddinggo.notification.service;

import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationRequest;
import com.biddingmate.biddinggo.notification.mapper.NotificationMapper;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final NotificationService notificationService;
    private final MemberMapper memberMapper;
    private final NotificationMapper notificationMapper;

    // 알람 발신을 위한 매서드
    public void publishNotification(Long receiverId, NotificationType type, String content, String url) {

        try {
            notificationService.createNotification(
                    CreateNotificationRequest.builder()
                            .receiverId(receiverId)
                            .type(type)
                            .content(content)
                            .url(url)
                            .build()
            );
        } catch (Exception e) {

            log.warn("[notification-failed] receiverId={}, type={}", receiverId, type, e);
        }
    }

    public void publishToActiveAdmins(NotificationType type, String content, String url) {

        List<Long> adminIds = memberMapper.findAllActiveAdminIds();
        if (adminIds == null || adminIds.isEmpty()) {
            return;
        }

        for (Long adminId : adminIds) {
            publishNotification(adminId, type, content, url);
        }

    }

    public void publishToActiveUsers(NotificationType type, String content, String url) {

        List<Long> memberIds = memberMapper.findAllActiveMemberIds();
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        for (Long memberId : memberIds) {
            publishNotification(memberId, type, content, url);
        }
    }

    public void removeNoticeNotifications(Long noticeId) {

        notificationMapper.deleteAdminNoticeNotificationsByNoticeId(noticeId);

    }
}
