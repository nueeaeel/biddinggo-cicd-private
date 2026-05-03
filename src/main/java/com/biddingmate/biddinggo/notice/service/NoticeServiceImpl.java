package com.biddingmate.biddinggo.notice.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;
import com.biddingmate.biddinggo.notice.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NoticeResponse> findActiveNotices(BasePageRequest request) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }

        List<NoticeResponse> list = noticeMapper.findActiveNotices(rowBounds, order.toUpperCase());
        int total = noticeMapper.getActiveNoticeTotal();

        return PageResponse.of(list, request.getPage(), request.getSize(), total);
    }

    @Override
    public PageResponse<NoticeResponse> findAllNotices(BasePageRequest request) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }

        List<NoticeResponse> list = noticeMapper.findAllNotices(rowBounds,order.toUpperCase());
        int total = noticeMapper.getNoticeTotal();
        return PageResponse.of(list, request.getPage(), request.getSize(), total);
    }
}
