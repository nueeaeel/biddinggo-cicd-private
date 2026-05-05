package com.biddingmate.biddinggo.member.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.dto.MemberBiddingItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberDashboardResponse;
import com.biddingmate.biddinggo.member.dto.MemberListView;
import com.biddingmate.biddinggo.member.dto.MemberListViewRequest;
import com.biddingmate.biddinggo.member.dto.MemberProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberProfileUpdateRequest;
import com.biddingmate.biddinggo.member.dto.MemberPurchaseItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResultResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionSummaryResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSellerProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResponse;
import com.biddingmate.biddinggo.member.dto.MemberWonItemResponse;
import com.biddingmate.biddinggo.member.dto.UpdateMemberStatusRequest;
import com.biddingmate.biddinggo.member.event.MemberStatusUpdateEvent;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.MemberGrade;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private static final long SILVER_MIN_CONFIRMED_DEAL_COUNT = 5L;
    private static final long GOLD_MIN_CONFIRMED_DEAL_COUNT = 20L;

    private final MemberMapper memberMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public MemberDashboardResponse getMyDashboard(Long memberId) {

        // 회원 존재 여부 체크
        memberExists(memberId);

        // 회원 정보 기본 조회
        MemberDashboardResponse summary = memberMapper.findDashboardInfoById(memberId);

        // 진행 중인 구매 현황 조회
        List<MemberWonItemResponse> purchaseItems = memberMapper.findPurchaseItemsById(memberId);

        // 진행 중인 판매 현황 조회
        List<MemberSalesItemResponse> salesItems = memberMapper.findSalesItemsById(memberId);

        // 입찰 내역 조회
        List<MemberBiddingItemResponse> biddingItems = memberMapper.findBiddingItemsById(memberId);

        // 대시보드 응답 DTO
        return MemberDashboardResponse.builder()
                .nickname(summary.getNickname())
                .grade(summary.getGrade())
                .point(summary.getPoint())
                .imageUrl(summary.getImageUrl())
                .purchaseItems(purchaseItems)
                .biddingItems(biddingItems)
                .salesItems(salesItems)
                .build();
    }

    @Override
    public MemberProfileResponse getMyProfile(Long memberId) {

        // 회원 존재 여부 체크
        memberExists(memberId);

        // 프로필 정보 조회
        return memberMapper.findProfileById(memberId);
    }

    @Override
    public MemberProfileResponse updateMyProfile(Long memberId, MemberProfileUpdateRequest request) {

        // 회원 존재 여부 확인
        Member member = getMember(memberId);

        // 닉네임 변경 요청 시
        if (isNicknameChanged(member, request)) {

            // 마지막 닉네임 변경일 기준 30일 지났는지 체크
            validateNickNameChangePeriod(member);

            // 닉네임 중복 체크
            validateDuplicateNickname(request.getNickname());
        }

        // 프로필 수정
        memberMapper.updateProfile(memberId, request);

        // 수정된 프로필 정보 반환
        return memberMapper.findProfileById(memberId);
    }

    @Override
    public void deleteMyAccount(Long memberId) {

        // 회원 존재 여부 확인
        Member member = getMember(memberId);

        // 이미 탈퇴한 회원이라면 예외 처리
        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorType.ALREADY_DELETED_MEMBER);
        }

        // 판매중인 경매가 있는 경우 탈퇴 불가
        if (memberMapper.existsOngoingSales(memberId)) {
            throw new CustomException(ErrorType.CANNOT_DELETE_MEMBER_WITH_ONGOING_SALES);
        }

        // 입찰중인 경매가 있는 경우 탈퇴 불가
        if (memberMapper.existsOngoingBids(memberId)) {
            throw new CustomException(ErrorType.CANNOT_DELETE_MEMBER_WITH_ONGOING_BIDS);
        }

        // 거래 미완료 건이 있는 경우 탈퇴 불가
        if (memberMapper.existsIncompleteDeals(memberId)) {
            throw new CustomException(ErrorType.CANNOT_DELETE_MEMBER_WITH_INCOMPLETE_DEALS);
        }

        // 탈퇴 처리 (soft delete)
        memberMapper.deleteMember(memberId);
        eventPublisher.publishEvent(new MemberStatusUpdateEvent(memberId, MemberStatus.DELETED));
    }

    @Override
    public PageResponse<MemberSalesItemResponse> getMySales(Long memberId, BasePageRequest pageRequest) {

        // 회원 존재 여부 확인
        getMember(memberId);

        // 정렬값은 최신순으로
        if (pageRequest.getOrder() == null || pageRequest.getOrder().isBlank()) {
            pageRequest.setOrder("DESC");
        }

        // 판매내역 목록 조회
        List<MemberSalesItemResponse> content =
                memberMapper.findSalesByMember(memberId, pageRequest);

        // 전체 개수 조회
        long totalElements = memberMapper.countSalesByMemberId(memberId);

        return PageResponse.of(
                content,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalElements
        );
    }

    @Override
    public PageResponse<MemberPurchaseItemResponse> getMyPurchases(Long memberId, BasePageRequest pageRequest) {

        // 회원 존재 여부 확인
        getMember(memberId);

        // 정렬값은 최신순으로
        if (pageRequest.getOrder() == null || pageRequest.getOrder().isBlank()) {
            pageRequest.setOrder("DESC");
        }

        // 구매내역 목록 조회
        List<MemberPurchaseItemResponse> content =
                memberMapper.findPurchasesByMemberId(memberId, pageRequest);

        // 전체 개수 조회
        long totalElements = memberMapper.countPurchasesByMemberId(memberId);

        return PageResponse.of(
                content,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalElements
        );
    }
  
    @Override
    @Transactional(readOnly = true)
    public long getCurrentPoint(Long memberId) {
        return getMember(memberId).getPoint();
    }

    @Override
    public void addPoint(Long memberId, Long amount) {
        memberMapper.addPoint(memberId, amount);
    }

    @Override
    @Transactional
    public void deductPoint(Long memberId, Long amount) {
        memberMapper.usePoint(memberId, amount);
    }

    @Override
    public MemberSalesAuctionResultResponse getMySalesAuctions(Long memberId, String type, BasePageRequest pageRequest) {

        // 회원 존재 여부 확인
        memberExists(memberId);

        // 상태값 검증
        validateSalesAuctionStatus(type);

        // 정렬값은 최신순으로
        if (pageRequest.getOrder() == null || pageRequest.getOrder().isBlank()) {
            pageRequest.setOrder("DESC");
        }

        String normalizedStatus = type.toUpperCase();

        // 판매 상품 목록 조회
        List<MemberSalesAuctionResponse> content =
                memberMapper.findMySalesAuctions(memberId, normalizedStatus, pageRequest);

        // 현재 선택한 상태 기준, 전체 개수 조회
        long totalElements =
                memberMapper.countMySalesAuctions(memberId, normalizedStatus);

        PageResponse<MemberSalesAuctionResponse> auctions = PageResponse.of(
                content,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalElements
        );

        MemberSalesAuctionSummaryResponse summary =
                memberMapper.countMySalesAuctionSummary(memberId);

        return MemberSalesAuctionResultResponse.builder()
                .summary(summary)
                .auctions(auctions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberListView> findAllMemberWithFilter(MemberListViewRequest request) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<MemberListView> list;
        int count;

        list = memberMapper.findAllWithFilter(rowBounds, sortOrder, request.getKeyword(), request.getStatus());
        count = memberMapper.countTotalMember(request.getKeyword(), request.getStatus());

        return PageResponse.of(list, request.getPage(), request.getSize(), count);
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long memberId, UpdateMemberStatusRequest request) {
        MemberStatus status = request.getStatus();

        if (status != MemberStatus.ACTIVE && status != MemberStatus.INACTIVE) {
            throw new CustomException(ErrorType.INVALID_ENUM_TYPE);
        }

        memberMapper.updateMemberStatus(memberId, status);

        // 이벤트 발행
        eventPublisher.publishEvent(new MemberStatusUpdateEvent(memberId, status));
    }

    // 회원 존재 여부 확인
    private void memberExists(Long memberId) {

        Member member = memberMapper.findById(memberId);

        // 회원 존재하지 않을 시, 예외처리
        if (member == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }
    }

    // 닉네임 변경 요청 확인 > 요청 닉네임이 존재하고, 공백이 아니고, 기존 닉네임과 다르면 true
    private boolean isNicknameChanged(Member member, MemberProfileUpdateRequest request) {
        return request.getNickname() != null
                && !request.getNickname().isBlank()
                && !Objects.equals(member.getNickname(), request.getNickname());
    }

    // 닉네임 변경 주기 확인 > lastChangeNick이 null이면 최초 변경이므로
    // 마지막 변경일로부터 30일이 지나지 않았으면 예외 발생
    private void validateNickNameChangePeriod(Member member) {
        if (member.getLastChangeNick() == null) {
            return;
        }

        LocalDateTime changeableDate = member.getLastChangeNick().plusDays(30);

        if (LocalDateTime.now().isBefore(changeableDate)) {
            throw new CustomException(ErrorType.INVALID_NICKNAME_CHANGE_PERIOD);
        }
    }

    // 동일한 닉네임이 존재하면 예외 발생
    private void validateDuplicateNickname(String nickname) {
        int count = memberMapper.countByNickname(nickname);

        if (count > 0) {
            throw new CustomException(ErrorType.DUPLICATED_NICKNAME);
        }
    }

    // 회원 조회
    private Member getMember(Long memberId) {
        Member member = memberMapper.findById(memberId);

        if (member == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }

        return member;
    }
    @Override
    @Transactional(readOnly = true)
    public MemberSellerProfileResponse getSellerProfile(Long sellerId) {

        getMember(sellerId);

        MemberSellerProfileResponse stats = memberMapper.findSellerStats(sellerId);

        // 결과값이 없을 경우 예외 처리
        if (stats == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }

        return stats;
    }
  
    @Override
    @Transactional
    public void recalculateMemberGrade(Long memberId) {
        getMember(memberId);

        long confirmedDealCount = memberMapper.countConfirmedDealsByMemberId(memberId);
        MemberGrade grade = resolveMemberGrade(confirmedDealCount);

        memberMapper.updateMemberGrade(memberId, grade);
    }

    private MemberGrade resolveMemberGrade(long confirmedDealCount) {
        if (confirmedDealCount >= GOLD_MIN_CONFIRMED_DEAL_COUNT) {
            return MemberGrade.GOLD;
        }

        if (confirmedDealCount >= SILVER_MIN_CONFIRMED_DEAL_COUNT) {
            return MemberGrade.SILVER;
        }

        return MemberGrade.BRONZE;
    } 
  
    private void validateSalesAuctionStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new CustomException(ErrorType.INVALID_AUCTION_STATUS);
        }

        String normalizedStatus = status.toUpperCase();

        if (!"ALL".equals(normalizedStatus)
                && !"PENDING".equals(normalizedStatus)
                && !"ONGOING".equals(normalizedStatus)
                && !"SUCCESS".equals(normalizedStatus)
                && !"FAILED".equals(normalizedStatus)
                && !"CANCELLED".equals(normalizedStatus)) {
            throw new CustomException(ErrorType.INVALID_AUCTION_STATUS);
        }
    }
}
