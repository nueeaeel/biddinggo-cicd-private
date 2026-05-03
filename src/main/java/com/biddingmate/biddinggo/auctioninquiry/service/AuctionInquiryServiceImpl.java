package com.biddingmate.biddinggo.auctioninquiry.service;

import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auctioninquiry.dto.AnswerAuctionInquiryRequest;
import com.biddingmate.biddinggo.auctioninquiry.dto.AnswerAuctionInquiryResponse;
import com.biddingmate.biddinggo.auctioninquiry.dto.AuctionInquiryView;
import com.biddingmate.biddinggo.auctioninquiry.dto.CreateAuctionInquiryRequest;
import com.biddingmate.biddinggo.auctioninquiry.dto.CreateAuctionInquiryResponse;
import com.biddingmate.biddinggo.auctioninquiry.dto.MemberAuctionInquiryResponse;
import com.biddingmate.biddinggo.auctioninquiry.mapper.AuctionInquiryMapper;
import com.biddingmate.biddinggo.auctioninquiry.model.AuctionInquiry;
import com.biddingmate.biddinggo.auctioninquiry.model.AuctionInquiryStatus;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import com.biddingmate.biddinggo.notification.service.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionInquiryServiceImpl implements AuctionInquiryService {

    private final AuctionInquiryMapper auctionInquiryMapper;
    private final AuctionMapper auctionMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public CreateAuctionInquiryResponse createInquiry(Long auctionId, Long writerId, CreateAuctionInquiryRequest request) {

        // 경매 존재 여부 검증
        Auction auction = validateAndGetAuction(auctionId);

        // 본인 경매 문의 제한
        if (auction.getSellerId().equals(writerId)) {
            throw new CustomException(ErrorType.CANNOT_INQUIRE_OWN_AUCTION);
        }

        AuctionInquiry inquiry = AuctionInquiry.builder()
                .auctionId(auctionId)
                .writerId(writerId)
                .answererId(auction.getSellerId())
                .title(request.getTitle())
                .content(request.getContent())
                .secretYn(request.isSecretYn())
                .status(AuctionInquiryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        int result = auctionInquiryMapper.insert(inquiry);

        if (result <= 0) {
            throw new CustomException(ErrorType.AUCTION_INQUIRY_CREATE_FAIL);
        }

        notificationPublisher.publishNotification(
                auction.getSellerId(),
                NotificationType.AUCTION_INQUIRY,
                "경매 #" + auctionId + "에 새로운 문의가 등록되었습니다.",
                "/auctions/" + auctionId + "/inquiries"
        );



        return CreateAuctionInquiryResponse.builder()
                .id(inquiry.getId())
                .auctionId(inquiry.getAuctionId())
                .writerId(inquiry.getWriterId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public AnswerAuctionInquiryResponse registerAnswer(Long inquiryId, Long sellerId, AnswerAuctionInquiryRequest request) {

        // 문의글 존재 여부 확인
        AuctionInquiry inquiry = auctionInquiryMapper.findInquiryById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorType.AUCTION_INQUIRY_NOT_FOUND));

        // 이미 답변이 달렸는지 확인
        if (inquiry.getStatus() == AuctionInquiryStatus.ANSWERED) {
            throw new CustomException(ErrorType.AUCTION_INQUIRY_ALREADY_ANSWERED);
        }

        // 답변 권한 확인
        if (inquiry.getAnswererId() == null || !inquiry.getAnswererId().equals(sellerId)) {
            throw new CustomException(ErrorType.FORBIDDEN);
        }

        // 답변 데이터 설정
        LocalDateTime now = LocalDateTime.now();
        inquiry.setAnswer(request.getAnswer());
        inquiry.setAnsweredAt(now);
        inquiry.setStatus(AuctionInquiryStatus.ANSWERED);

        int result = auctionInquiryMapper.updateAnswer(inquiry);
        if (result <= 0) {
            throw new CustomException(ErrorType.AUCTION_INQUIRY_UPDATE_FAIL);
        }

        notificationPublisher.publishNotification(
                inquiry.getWriterId(),
                NotificationType.AUCTION_INQUIRY,
                "문의 #" + inquiry.getId() + "에 판매자 답변이 등록되었습니다,",
                "/auctions/" + inquiry.getAuctionId() + "/inquiries"
        );

        return AnswerAuctionInquiryResponse.builder()
                .id(inquiry.getId())
                .answer(inquiry.getAnswer())
                .answeredAt(now)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuctionInquiryView> getInquiriesByAuctionId(
            Long auctionId,
            BasePageRequest request,
            Long currentUserId
    ) {

        // 경매 존재 여부 확인
        Auction auction = validateAndGetAuction(auctionId);

        // 페이지 번호 검증
        if (request.getPage() < 1) {
            throw new CustomException(ErrorType.BAD_REQUEST);
        }

        // DB 조회 
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        List<AuctionInquiryView> rawList = auctionInquiryMapper.selectInquiryList(rowBounds, auctionId);
        int totalCount = auctionInquiryMapper.selectInquiryCount(auctionId);

        // 불변 DTO 특성을 살린 권한별 마스킹 처리
        List<AuctionInquiryView> processedList = rawList.stream()
                .map(view -> {
                    // 작성자 본인인가? OR 해당 경매 판매자인가?
                    boolean isWriter = (currentUserId != null) && view.getWriterId().equals(currentUserId);
                    boolean isSeller = (currentUserId != null) && auction.getSellerId().equals(currentUserId);

                    boolean hasFullAccess = isWriter || isSeller;

                    // 권한이 있는 경우: 원본 그대로 반환
                    if (hasFullAccess) {
                        return view;
                    }

                    // 권한이 없는 경우: 닉네임 마스킹 + 비밀글 여부에 따른 내용 마스킹
                    AuctionInquiryView maskedView = view.withMaskedWriterName();
                    if (maskedView.isSecretYn()) {
                        maskedView = maskedView.withSecretMasking();
                    }
                    return maskedView;
                })
                .toList();

        return PageResponse.of(processedList, request.getPage(), request.getSize(), totalCount);
    }

    // 경매 존재 여부 확인 로직 공통 메서드
    private Auction validateAndGetAuction(Long auctionId) {
        Auction auction = auctionMapper.findById(auctionId);
        if (auction == null) {
            throw new CustomException(ErrorType.AUCTION_NOT_FOUND);
        }
        return auction;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberAuctionInquiryResponse> getMyAuctionInquiries(Long memberId, String type, BasePageRequest request) {

        // 조회 타입 유효성 검증 (ALL 구매,판매 모두 / PURCHASE 구매 / SALES 판매)
        validateInquiryType(type);

        // 정렬값 유효성 검증 (ASC / DESC, 기본값 DESC)
        validateOrder(request);

        // type 값을 대문자로 통일해서 mapper에서 일관되게 사용
        String normalizedType = type.toUpperCase();

        // 회원의 문의 내역 조회
        // type에 따라 전체 / 구매 / 판매 문의 필터링
        List<MemberAuctionInquiryResponse> content =
                auctionInquiryMapper.findMyAuctionInquiries(memberId, normalizedType, request);

        // 전체 문의 개수 조회
        long totalElements =
                auctionInquiryMapper.countMyAuctionInquiries(memberId, normalizedType);

        return PageResponse.of(content, request.getPage(), request.getSize(), totalElements);
    }

    // 문의 조회 타입 검증
    // 허용 값: ALL(전체), PURCHASE(구매 문의), SALES(판매 문의)
    private void validateInquiryType(String type) {

        // null 또는 공백 값 방지
        if (type == null || type.isBlank()) {
            throw new CustomException(ErrorType.BAD_REQUEST);
        }

        String normalizedType = type.toUpperCase();

        // 허용된 타입이 아닐 경우 예외 발생
        if (!normalizedType.equals("ALL")
                && !normalizedType.equals("PURCHASE") && !normalizedType.equals("SALES")) {
            throw new CustomException(ErrorType.BAD_REQUEST);
        }
    }

    // 정렬 조건 검증
    // 허용 값: ASC, DESC
    // 기본값: DESC (최신순)
    private void validateOrder(BasePageRequest request) {

        // 정렬 값이 없으면 기본값 DESC 설정
        if (request.getOrder() == null || request.getOrder().isBlank()) {
            request.setOrder("DESC");
            return;
        }

        String order = request.getOrder().toUpperCase();

        // ASC, DESC 외 값이면 DESC으로 설정
        if (!order.equals("ASC") && !order.equals("DESC")) {
            request.setOrder("DESC");
            return;
        }

        // 정상 값이면 대문자로 통일해서 정렬 값 설정
        request.setOrder(order);
    }
}
