package com.biddingmate.biddinggo.notice.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;
import com.biddingmate.biddinggo.notice.vo.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface NoticeMapper extends IMybatisCRUD<Notice> {

    List<NoticeResponse> findActiveNotices(RowBounds rowBounds, String upperCase);

    List<NoticeResponse> findAllNotices(RowBounds rowBounds, String upperCase);

    int getActiveNoticeTotal();

    int getNoticeTotal();
}
