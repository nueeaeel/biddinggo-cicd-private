package com.biddingmate.biddinggo.review.service;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.review.dto.CreateReviewRequest;
import com.biddingmate.biddinggo.review.dto.CreateReviewResponse;
import com.biddingmate.biddinggo.review.dto.SellerReviewResponse;

public interface ReviewService {
    // 리뷰 등록
    CreateReviewResponse createReview(Long auctionId, Member member, CreateReviewRequest request);

    // 판매자 리뷰 조회
    PageResponse<SellerReviewResponse> getSellerReviews(Long sellerId, BasePageRequest pageRequest);
}