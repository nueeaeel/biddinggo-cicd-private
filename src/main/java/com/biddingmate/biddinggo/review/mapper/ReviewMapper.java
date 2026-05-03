package com.biddingmate.biddinggo.review.mapper;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.review.dto.SellerReviewResponse;
import com.biddingmate.biddinggo.review.model.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    // 리뷰 저장
    int insertReview(Review review);

    // 동일한 거래에 대해 이미 리뷰를 썼는지 확인용
    int countByDealIdAndWriterId(@Param("dealId") Long dealId, @Param("writerId") Long writerId);

    // 판매자 리뷰 목록 조회
    List<SellerReviewResponse> findSellerReviewsBySellerId(
            @Param("sellerId") Long sellerId, @Param("pageRequest") BasePageRequest pageRequest);

    // 판매자 리뷰 전체 개수 조회
    long countSellerReviewsBySellerId(@Param("sellerId") Long sellerId);
}