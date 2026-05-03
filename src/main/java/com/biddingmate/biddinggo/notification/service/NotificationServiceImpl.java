package com.biddingmate.biddinggo.notification.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationRequest;
import com.biddingmate.biddinggo.notification.dto.CreateNotificationResponse;
import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import com.biddingmate.biddinggo.notification.mapper.NotificationMapper;
import com.biddingmate.biddinggo.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final MemberMapper memberMapper;
    private final NotificationSseService notificationSseService;

    @Override
    public CreateNotificationResponse createNotification(CreateNotificationRequest request) {

        // 입력값 확인
        if (memberMapper.findById(request.getReceiverId()) == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }

        Notification notification = Notification.builder()
                .receiverId(request.getReceiverId())
                .type(request.getType())
                .content(request.getContent())
                .url(request.getUrl())
                .createdAt(LocalDateTime.now())
                .build();

        int result = notificationMapper.insert(notification);

        if(result != 1 || notification.getId() == null){
            throw new CustomException(ErrorType.NOTIFICATION_SAVE_FAILED);
        }

        NotificationResponse response = NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .url(notification.getUrl())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
        notificationSseService.send(notification.getReceiverId(), response);

        return CreateNotificationResponse.builder()
                .id(notification.getId())
                .build();
    }

    @Override
    public PageResponse<NotificationResponse> getNotificationsByMemberId(BasePageRequest request, Long receiverId) {

        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<NotificationResponse> notifications = notificationMapper.getNotificationsByMemberId(rowBounds, receiverId, sortOrder);
        int bidCount = notificationMapper.getNotificationCount(Map.of("receiverId", receiverId));

        return PageResponse.of(notifications, request.getPage(), request.getSize(), bidCount);

    }

    @Override
    public void markAllAsRead(Long receiverId) {
        notificationMapper.updateReadAtByMemberId(receiverId);
    }

    @Override
    public void markAsRead(Long id, Long receiverId) {
        notificationMapper.updateReadAtById(id, receiverId);
    }

    @Override
    public int countUnread(Long memberId) {

        return notificationMapper.countUnreadByReceiverId(memberId);
    }
}
