package com.biddingmate.biddinggo.review.service;

import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.review.dto.CreateReviewRequest;
import com.biddingmate.biddinggo.review.dto.CreateReviewResponse;
import com.biddingmate.biddinggo.review.dto.SellerReviewResponse;
import com.biddingmate.biddinggo.review.mapper.ReviewMapper;
import com.biddingmate.biddinggo.review.model.Review;
import com.biddingmate.biddinggo.winnerdeal.mapper.WinnerDealMapper;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final AuctionMapper auctionMapper;
    private final WinnerDealMapper winnerDealMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public CreateReviewResponse createReview(Long auctionId, Member member, CreateReviewRequest request) {

        Long writerId = member.getId();

        // 옥션 정보 조회
        Auction auction = auctionMapper.findById(auctionId);
        if (auction == null) throw new CustomException(ErrorType.AUCTION_NOT_FOUND);

        // 권한 체크 (조회한 유저 ID와 낙찰자 ID 대조)
        if (!writerId.equals(auction.getWinnerId())) {
            throw new CustomException(ErrorType.NOT_THE_WINNER);
        }

        // 낙찰 정보 조회
        var deal = winnerDealMapper.findByAuctionId(auctionId);
        if (deal == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        if (deal.getStatus() != WinnerDealStatus.CONFIRMED) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_CONFIRMED);
        }

        // 중복 리뷰 방지
        int count = reviewMapper.countByDealIdAndWriterId(deal.getId(), writerId);
        if (count > 0) {
            throw new CustomException(ErrorType.ALREADY_REVIEWED);
        }

        // 리뷰 저장 (조회된 writerId 사용)
        Review review = Review.builder()
                .dealId(deal.getId())
                .writerId(writerId)
                .targetId(auction.getSellerId())
                .rating(request.getRating())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        reviewMapper.insertReview(review);

        return CreateReviewResponse.builder()
                .id(review.getId())
                .auctionId(auctionId)
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    public PageResponse<SellerReviewResponse> getSellerReviews(Long sellerId, BasePageRequest pageRequest) {

        // 판매자 존재 여부 체크
        getMember(sellerId);

        // ASC DESC 유효한 정렬값인지 체크
        validateOrder(pageRequest);

        // 리뷰 목록 조회
        List<SellerReviewResponse> content = reviewMapper.findSellerReviewsBySellerId(sellerId, pageRequest);

        // 전체 개수 조회
        long totalElements = reviewMapper.countSellerReviewsBySellerId(sellerId);

        return PageResponse.of(
                content,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalElements
        );

    }

    private void getMember(Long sellerId) {
        Member member = memberMapper.findById(sellerId);

        if (member == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }
    }

    private void validateOrder(BasePageRequest pageRequest) {

        // null 또는 빈값이면 DESC 정렬
        if (pageRequest.getOrder() == null || pageRequest.getOrder().isBlank()) {
            pageRequest.setOrder("DESC");
            return;
        }

        String order = pageRequest.getOrder().toUpperCase();

        // ASC 또는 DESC만 받기
        if (!order.equals("ASC") && !order.equals("DESC")) {
            pageRequest.setOrder("DESC");
            return;
        }

        pageRequest.setOrder(order);
    }
}