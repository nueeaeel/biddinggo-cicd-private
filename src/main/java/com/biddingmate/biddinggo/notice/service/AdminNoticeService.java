package com.biddingmate.biddinggo.notice.service;

import com.biddingmate.biddinggo.notice.dto.CreateNoticeRequest;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;
import com.biddingmate.biddinggo.notice.dto.UpdateNoticeRequest;
import jakarta.validation.Valid;

public interface AdminNoticeService {
    NoticeResponse create(@Valid CreateNoticeRequest request);

    NoticeResponse update(Long noticeId, @Valid UpdateNoticeRequest request);

    void delete(Long noticeId);
}
