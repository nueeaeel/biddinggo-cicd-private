package com.biddingmate.biddinggo.notification.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.notification.dto.NotificationResponse;
import com.biddingmate.biddinggo.notification.model.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper extends IMybatisCRUD<Notification> {
    List<NotificationResponse> getNotificationsByMemberId(RowBounds rowBounds,
                                                          @Param("receiverId") Long receiverId,
                                                          @Param("order") String sortOrder);

    int getNotificationCount(Map<String, Long> params);

    void updateReadAtByMemberId(@Param("receiverId") Long receiverId);

    void updateReadAtById(@Param("id") Long id, @Param("receiverId") Long receiverId);

    int countUnreadByReceiverId(@Param("receiverId") Long receiverId);

    int deleteAdminNoticeNotificationsByNoticeId(@Param("noticeId") Long noticeId);
}
