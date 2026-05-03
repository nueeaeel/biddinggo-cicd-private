package com.biddingmate.biddinggo.member.mapper;

import com.biddingmate.biddinggo.auth.dto.SocialInfoUpdateDto;
import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.member.dto.MemberBiddingItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberDashboardResponse;
import com.biddingmate.biddinggo.member.dto.MemberListView;
import com.biddingmate.biddinggo.member.dto.MemberProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberProfileUpdateRequest;
import com.biddingmate.biddinggo.member.dto.MemberPurchaseItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionSummaryResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSellerProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResponse;
import com.biddingmate.biddinggo.member.dto.MemberWonItemResponse;
import com.biddingmate.biddinggo.member.model.MemberGrade;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface MemberMapper extends IMybatisCRUD<Member> {
    void addPoint(@Param("id") Long id, @Param("amount") Long amount);

    void usePoint(@Param("id") Long id, @Param("amount") Long amount);

    Long getPointById(@Param("id") Long id);

    MemberProfileResponse findProfileById(@Param("memberId") Long memberId);

    // 대시보드 프로필 기본 정보
    MemberDashboardResponse findDashboardInfoById(@Param("memberId") Long memberId);

    // 진행 중 구매 현황
    List<MemberWonItemResponse> findPurchaseItemsById(@Param("memberId") Long memberId);

    // 입찰 내역
    List<MemberBiddingItemResponse> findBiddingItemsById(@Param("memberId") Long memberId);

    // 진행 중 판매 현황
    List<MemberSalesItemResponse> findSalesItemsById(@Param("memberId") Long memberId);

    // 아이디를 통한 사용자 조회(auth)
    Member selectMemberByUsername(@Param("username") String username);

    // 이메일을 통한 사용자 조회(auth)
    Member selectMemberByEmail(@Param("email") String email);


    Member selectMemberByNickname(@Param("nickname") String nickname);

    int saveMember(Member member);

    int updateMember(Member member);

    void updateMemberInfo(SocialInfoUpdateDto updateDto);

    void updateProfile(@Param("memberId") Long memberId, @Param("request") MemberProfileUpdateRequest request);

    // 수정할 닉네임이 사용 중 인지 확인
    int countByNickname(@Param("nickname") String nickname);

    // member status를 DELETED로 변경
    void deleteMember(@Param("memberId") Long memberId);

    String findUsernameById(@Param("memberId") Long memberId);

    // 판매내역 목록
    List<MemberSalesItemResponse> findSalesByMember(
            @Param("memberId") Long memberId,
            @Param("pageRequest") BasePageRequest pageRequest
    );

    // 총 판매 개수 조회
    long countSalesByMemberId(@Param("memberId") Long memberId);

    // 구매내역 목록
    List<MemberPurchaseItemResponse> findPurchasesByMemberId(
            @Param("memberId") Long memberId,
            @Param("pageRequest") BasePageRequest pageRequest
    );

    // 총 구매 개수 조회
    long countPurchasesByMemberId(@Param("memberId") Long memberId);

    List<MemberSalesAuctionResponse> findMySalesAuctions(
            @Param("memberId") Long memberId,
            @Param("type") String type,
            @Param("pageRequest") BasePageRequest pageRequest
    );

    long countMySalesAuctions(@Param("memberId") Long memberId, @Param("type") String type);

    List<MemberListView> findAllWithFilter(RowBounds rowBounds, String sortOrder, String keyword, MemberStatus status);

    int countTotalMember(String keyword, MemberStatus status);

    void updateMemberStatus(@Param("memberId") Long memberId,
                            @Param("status") MemberStatus status);

    MemberSellerProfileResponse findSellerStats(@Param("memberId") Long memberId);

    // 판매중인 경매 존재 여부
    boolean existsOngoingSales(@Param("memberId") Long memberId);

    // 입출중인 경매 존재 여부
    boolean existsOngoingBids(@Param("memberId") Long memberId);

    // 거래 미완료 존재 여부
    boolean existsIncompleteDeals(@Param("memberId") Long memberId);

    // 사용자 VIP 등급 재산정을 위한 거래 건수 측정
    long countConfirmedDealsByMemberId(@Param("memberId") Long memberId);

    // 사용자 VIP 등급 재산정
    void updateMemberGrade(@Param("memberId") Long memberId,
                           @Param("grade") MemberGrade grade);

    MemberSalesAuctionSummaryResponse countMySalesAuctionSummary(@Param("memberId") Long memberId);

    Member findByIdForUpdate(@Param("id") Long id);

    List<Long> findAllActiveMemberIds();

    List<Long> findAllActiveAdminIds();

}
