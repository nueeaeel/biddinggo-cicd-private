package com.biddingmate.biddinggo.notice.service;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;

public interface NoticeService {
    PageResponse<NoticeResponse> findActiveNotices(BasePageRequest request);

    PageResponse<NoticeResponse> findAllNotices(BasePageRequest request);


}
