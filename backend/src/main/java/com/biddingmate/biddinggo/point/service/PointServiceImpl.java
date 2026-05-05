package com.biddingmate.biddinggo.point.service;

import com.biddingmate.biddinggo.auction.dto.RefundDto;
import com.biddingmate.biddinggo.bid.service.BidQueryService;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.service.MemberService;
import com.biddingmate.biddinggo.point.dto.ExchangePointRequest;
import com.biddingmate.biddinggo.point.dto.MyPointResponse;
import com.biddingmate.biddinggo.point.dto.PointHistoryDto;
import com.biddingmate.biddinggo.point.mapper.PointHistoryMapper;
import com.biddingmate.biddinggo.point.model.PointHistory;
import com.biddingmate.biddinggo.point.model.PointHistoryType;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final MemberService memberService;
    private final BidQueryService bidQueryService;
    private final PointHistoryMapper pointHistoryMapper;

    @Override
    public int addPointHistory(PointHistory pointHistory) {
        return pointHistoryMapper.insert(pointHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public MyPointResponse findMyPointList(BasePageRequest request, Long memberId) {
        long currentPoint = memberService.getCurrentPoint(memberId);

        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<PointHistoryDto> list;
        int count;

        list = pointHistoryMapper.findByMemberId(rowBounds, sortOrder, memberId);
        count = pointHistoryMapper.countByMemberId(memberId);

        PageResponse<PointHistoryDto> pointhistory = PageResponse.of(list, request.getPage(), request.getSize(), count);

        return MyPointResponse.builder()
                .currentPoint(currentPoint)
                .histroies(pointhistory)
                .build();
    }

    @Override
    @Transactional
    public void exchangePoint(ExchangePointRequest request, Long memberId) {
        memberService.deductPoint(memberId, request.getAmount());

        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .type(PointHistoryType.EXCHANGE)
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        int insert = this.addPointHistory(pointHistory);

        if (insert != 1) {
            throw new CustomException(ErrorType.POINT_HISTORY_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void refundBid(Long bidderId, Long amount) {
        memberService.addPoint(bidderId, amount);

        PointHistory pointHistory = PointHistory.builder()
                .memberId(bidderId)
                .type(PointHistoryType.REFUND)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        int refund = this.addPointHistory(pointHistory);
        if (refund != 1) {
            throw new CustomException(ErrorType.POINT_HISTORY_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void settleWinnerDeal(Long sellerId, Long amount) {
        memberService.addPoint(sellerId, amount);

        PointHistory pointHistory = PointHistory.builder()
                .memberId(sellerId)
                .type(PointHistoryType.SETTLEMENT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        int settlement = this.addPointHistory(pointHistory);
        if (settlement != 1) {
            throw new CustomException(ErrorType.POINT_HISTORY_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void refundBidsByAuctionIds(List<Long> auctionIds) {
        // 1. 해당 경매들 입찰 전체 조회
        List<RefundDto> refunds = bidQueryService.findByAuctionIds(auctionIds);

        // 2. 환불 처리
        for (RefundDto refund : refunds) {
            refundBid(refund.getBidderId(), refund.getAmount());
        }
    }
}
