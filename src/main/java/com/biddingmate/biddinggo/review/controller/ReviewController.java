package com.biddingmate.biddinggo.review.controller;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.review.dto.CreateReviewRequest;
import com.biddingmate.biddinggo.review.dto.CreateReviewResponse;
import com.biddingmate.biddinggo.review.dto.SellerReviewResponse;
import com.biddingmate.biddinggo.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "경매 건에 대해 리뷰를 등록합니다.")
    @PostMapping("/{auctionId}/reviews")
    public ResponseEntity<ApiResponse<CreateReviewResponse>> createReview(
            @PathVariable Long auctionId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Member member
    ) {
        if (member == null) {
            throw new CustomException(ErrorType.USER_NOT_FOUND);
        }

        CreateReviewResponse result = reviewService.createReview(auctionId, member, request);

        return ApiResponse.of(HttpStatus.OK, "success", "리뷰 등록 성공", result);
    }

    @Operation(summary = "리뷰 조회", description = "판매자에 대한 리뷰를 조회합니다.")
    @GetMapping("/{sellerId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<SellerReviewResponse>>> getSellerReviews(
            @PathVariable Long sellerId,
            @Valid BasePageRequest pageRequest
            ) {
        PageResponse<SellerReviewResponse> result = reviewService.getSellerReviews(sellerId, pageRequest);

        return ApiResponse.of(HttpStatus.OK, null, "판매자 리뷰 조회 성공", result);
    }
}