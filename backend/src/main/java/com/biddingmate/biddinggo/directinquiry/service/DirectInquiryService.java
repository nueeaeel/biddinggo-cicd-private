package com.biddingmate.biddinggo.directinquiry.service;

import com.biddingmate.biddinggo.directinquiry.dto.DirectInquiryView;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryResponse;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryResponse;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;

public interface DirectInquiryService {
    CreateDirectInquiryResponse createDirectInquiry(CreateDirectInquiryRequest request, Long memberId);
    PageResponse<DirectInquiryView> findDirectInquiry(BasePageRequest request, Long memberId);
    PageResponse<DirectInquiryView> findAllDirectInquiry(BasePageRequest request);
    AnswerDirectInquiryResponse answerDirectInquiry(Long inquiryId, AnswerDirectInquiryRequest request, Long adminId);


}
