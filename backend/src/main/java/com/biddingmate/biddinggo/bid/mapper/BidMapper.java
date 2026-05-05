package com.biddingmate.biddinggo.bid.mapper;

import com.biddingmate.biddinggo.auction.dto.RefundDto;
import com.biddingmate.biddinggo.bid.dto.BidListResponse;
import com.biddingmate.biddinggo.bid.dto.BidResponse;
import com.biddingmate.biddinggo.bid.model.Bid;
import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

@Mapper
public interface BidMapper extends IMybatisCRUD<Bid> {

    Bid getVickreyBid(@Param("auctionId") Long auctionId);

    Long getLastBidAmountByMemberId(@Param("auctionId") Long auctionId,
                                    @Param("bidderId") Long bidderId);

    int getBidCount(Map<String, Object> params);

    List<BidResponse> getBidsByAuctionId(RowBounds rowBounds,
                                         @Param("auctionId") Long auctionId,
                                         @Param("order") String sortOrder);

    List<BidListResponse> findBidAuctionsByMemberId(RowBounds rowBounds,
                                                    @Param("memberId") Long memberId,
                                                    @Param("order")String sortOrder);

    // 특정 회원이 참여한 진행 중 경매 목록 조회
    List<Long> findOngoingAuctionIdsByMember(@Param("memberId") Long memberId);

    // 활성화 중인 최고 입찰자 조회
    Long findTopBidderId(@Param("auctionId") Long auctionId);

    // 활성화 중인 상위 2개 입찰 조회 (비크리 핵심)
    List<Bid> findTop2ActiveBids(@Param("auctionId") Long auctionId);

    // 경매 id 목록으로 입찰내역 조회
    List<RefundDto> findRefundTargets(@Param("auctionIds") List<Long> auctionIds);

    // 비활성화 된 사용자의 최고 입찰 조회
    Long findMaxBidAmountByAuctionAndBidder(@Param("auctionId") Long auctionId,
                                            @Param("bidderId") Long memberId);

    Long findMaxBidAmountByAuctionAndBidder2(@Param("auctionId") Long auctionId,
                                             @Param("bidderId") Long bidderId);

    // 비활성화 처리 과정에서 bid 상태가 이미 INACTIVE로 바뀔 수 있으므로 상태와 무관하게 최고 입찰금을 조회.
    Long findMaxBidAmountByAuctionAndBidderRegardlessStatus(@Param("auctionId") Long auctionId,
                                                            @Param("bidderId") Long bidderId);

    // 비활성화 시 입찰 무효화
    void invalidateBidsByMember(@Param("memberId") Long memberId);

    // 낙찰자 제외 입찰자 환불 대상 조회
    List<RefundDto> findRefundTargetsExcludingWinner(@Param("auctionId") Long auctionId,
                                                     @Param("winnerId") Long winnerId);
}
