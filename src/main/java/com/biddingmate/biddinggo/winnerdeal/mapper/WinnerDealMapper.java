package com.biddingmate.biddinggo.winnerdeal.mapper;

import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealDetailQueryResult;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealShippingAddressRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealTrackingNumberRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealDetailQueryResult;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface WinnerDealMapper {
    int insert(WinnerDeal winnerDeal);
    WinnerDeal findById(Long id);
    WinnerDeal findByAuctionId(Long auctionId);
    boolean existsByDealNumber(@Param("dealNumber") String dealNumber);

    List<WinnerDeal> findByWinnerId(@Param("memberId") Long memberId);
    List<WinnerDeal> findBySellerId(@Param("memberId") Long memberId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    // 구매내역 조회
    List<WinnerDealHistoryResponse> findPurchaseHistory(RowBounds rowBounds,
                                                        @Param("request") WinnerDealHistoryRequest request,
                                                        @Param("memberId") Long memberId);
    long countPurchaseHistory(@Param("request") WinnerDealHistoryRequest request,
                              @Param("memberId") Long memberId);

    // 판매내역 조회
    List<WinnerDealHistoryResponse> findSaleHistory(RowBounds rowBounds,
                                                    @Param("request") WinnerDealHistoryRequest request,
                                                    @Param("memberId") Long memberId);
    long countSaleHistory(@Param("request") WinnerDealHistoryRequest request,
                          @Param("memberId") Long memberId);
    List<AdminWinnerDealListResponse> findAdminWinnerDealHistory(RowBounds rowBounds,
                                                                 @Param("request") AdminWinnerDealListRequest request,
                                                                 @Param("order") String order);
    long countAdminWinnerDealHistory(@Param("request") AdminWinnerDealListRequest request);
    AdminWinnerDealDetailQueryResult findAdminWinnerDealDetail(@Param("winnerDealId") Long winnerDealId);

    // 거래 내역 상세 조회
    WinnerDealDetailQueryResult findWinnerDealDetail(@Param("winnerDealId") Long winnerDealId);

    // 구매자의 배송지 등록
    int updateShippingAddress(@Param("winnerDealId") Long winnerDealId,
                              @Param("request") WinnerDealShippingAddressRequest request);

    int updateTrackingNumber(@Param("winnerDealId") Long winnerDealId,
                             @Param("request") WinnerDealTrackingNumberRequest request);

    int confirmPurchase(@Param("winnerDealId") Long winnerDealId,
                        @Param("confirmedAt") java.time.LocalDateTime confirmedAt);
}
