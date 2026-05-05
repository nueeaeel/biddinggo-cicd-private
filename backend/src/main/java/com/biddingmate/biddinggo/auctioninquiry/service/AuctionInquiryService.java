package com.biddingmate.biddinggo.auctioninquiry.service;

import com.biddingmate.biddinggo.auctioninquiry.dto.AnswerAuctionInquiryRequest;
import com.biddingmate.biddinggo.auctioninquiry.dto.AnswerAuctionInquiryResponse;
import com.biddingmate.biddinggo.auctioninquiry.dto.AuctionInquiryView;
import com.biddingmate.biddinggo.auctioninquiry.dto.CreateAuctionInquiryRequest;
import com.biddingmate.biddinggo.auctioninquiry.dto.CreateAuctionInquiryResponse;
import com.biddingmate.biddinggo.auctioninquiry.dto.MemberAuctionInquiryResponse;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import jakarta.validation.Valid;

public interface AuctionInquiryService {

    CreateAuctionInquiryResponse createInquiry(Long auctionId, Long writerId, CreateAuctionInquiryRequest request);

    AnswerAuctionInquiryResponse registerAnswer(Long inquiryId, Long sellerId, AnswerAuctionInquiryRequest request);

    PageResponse<AuctionInquiryView> getInquiriesByAuctionId(Long auctionId, BasePageRequest request, Long currentUserId);

    PageResponse<MemberAuctionInquiryResponse> getMyAuctionInquiries(Long memberId, String type, BasePageRequest request);
}