package com.biddingmate.biddinggo.notification.service;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationRequest;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationResponse;
import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import jakarta.validation.Valid;

public interface NotificationService{
    CreateNotificationResponse createNotification(@Valid CreateNotificationRequest request);

    PageResponse<NotificationResponse> getNotificationsByMemberId(BasePageRequest request, Long receiverId);

    void markAllAsRead(Long receiverId);

    void markAsRead(Long id, Long receiverId);

    int countUnread(Long memberId);
}
