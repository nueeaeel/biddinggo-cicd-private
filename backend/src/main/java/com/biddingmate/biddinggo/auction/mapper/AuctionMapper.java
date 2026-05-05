package com.biddingmate.biddinggo.auction.mapper;

import com.biddingmate.biddinggo.auction.dto.AuctionDetailResponse;
import com.biddingmate.biddinggo.auction.dto.AuctionListResponse;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuctionMapper extends IMybatisCRUD<Auction> {
    AuctionDetailResponse findDetailById(Long auctionId);

    List<AuctionListResponse> findAuctionList(RowBounds rowBounds,
                                              @Param("status") AuctionStatus status,
                                              @Param("sellerId") Long sellerId,
                                              @Param("categoryId") Long categoryId,
                                              @Param("sortBy") String sortBy,
                                              @Param("order") String order);

    int countAuctionList(@Param("status") AuctionStatus status,
                         @Param("sellerId") Long sellerId,
                         @Param("categoryId") Long categoryId);

    /**
     * Supabase에서 찾은 후보 auction ID 집합을 기준으로 실제 경매 목록을 조회한다.
     * 최종 정렬과 상태 필터는 MariaDB 기준으로 다시 적용한다.
     */
    List<AuctionListResponse> findAuctionListByAuctionIds(RowBounds rowBounds,
                                                          @Param("auctionIds") List<Long> auctionIds,
                                                          @Param("status") AuctionStatus status,
                                                          @Param("sortBy") String sortBy,
                                                          @Param("order") String order);

    /**
     * 후보 auction ID 집합 중 실제 조회 조건을 만족하는 경매 수를 반환한다.
     */
    int countAuctionListByAuctionIds(@Param("auctionIds") List<Long> auctionIds,
                                     @Param("status") AuctionStatus status);

    int updateAuction(Auction auction);

    int cancelAuction(@Param("auctionId") Long auctionId,
                      @Param("cancelDate") LocalDateTime cancelDate,
                      @Param("newStatus") AuctionStatus newStatus);

    void updateAfterBid(@Param("id") Long id, @Param("vickreyPrice") Long vickreyPrice);

    Auction findById(Long auctionId);

    Auction findByIdForUpdate(Long auctionId);

    void updateWishCount(@Param("id") Long id, @Param("wishCount") int wishCount);

    List<Long> findActiveAuctionIdsBySeller(@Param("sellerId") Long memberId);


    void updateAuctionStatus(@Param("auctionIds") List<Long> auctionIds,
                             @Param("status") AuctionStatus status);

    // 입찰 1위를 제외(비활성화)하고 남은 활성화 사용자들의 입찰이 1명 이하인 경우
    void resetVickreyPriceToStartPrice(@Param("auctionId") Long auctionId);

    // 비크리 차순위 승계
    void updateVickreyPrice(@Param("auctionId") Long auctionId,
                            @Param("amount") Long amount);

    void decreaseBidCountByDeactiveMember(@Param("memberId") Long memberId);
  
    List<Auction> findExpiredAuctions(@Param("now") LocalDateTime now,
                                      @Param("status") AuctionStatus status);

    int updateAuctionResult(Auction auction);

    int extendAuctionTime(@Param("id") Long id);
}
