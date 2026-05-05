package com.biddingmate.biddinggo.report.service;

import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.member.event.MemberStatusUpdateEvent;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import com.biddingmate.biddinggo.report.dto.ReportCreateRequest;
import com.biddingmate.biddinggo.report.mapper.ReportMapper;
import com.biddingmate.biddinggo.report.model.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final MemberMapper memberMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AuctionMapper auctionMapper;
    @Override
    @Transactional
    public void processReport(ReportCreateRequest request, Long reporterId) {
        // 경매 존재 확인
        Auction auction = auctionMapper.findById(request.getAuctionId());
        if (auction == null) {
            throw new CustomException(ErrorType.AUCTION_NOT_FOUND);
        }

        // 경매글에 등록된 판매자 ID와 신고하려는 대상 ID가 다를 경우
        if (!auction.getSellerId().equals(request.getTargetMemberId())) {
            throw new CustomException(ErrorType.INVALID_REPORT_TARGET);
        }

        //자기 자신 신고 검증
        if (request.getTargetMemberId().equals(reporterId)) {
            throw new CustomException(ErrorType.CANNOT_REPORT_SELF);
        }

        // 중복 신고 검증
        if (reportMapper.existsByReporterIdAndTargetId(reporterId, request.getTargetMemberId(), "MEMBER")) {
            throw new CustomException(ErrorType.ALREADY_REPORTED);
        }

        // 동시성 제어를 위한 유저 락 ( 대상 유저가 없을 경우)
        Member targetMember = memberMapper.findByIdForUpdate(request.getTargetMemberId());
        if (targetMember == null) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND);
        }

        // 신고 데이터 저장
        Report report = Report.builder()
                .reporterId(reporterId)
                .targetId(request.getTargetMemberId())
                .targetType("MEMBER")
                .reason(request.getReason())
                .build();

        if (reportMapper.insertReport(report) <= 0) {
            throw new CustomException(ErrorType.REPORT_CREATE_FAIL);
        }

        // 누적 횟수 확인 (10회 이상 시 커스텀 정지 로직 실행)
        int reportCount = reportMapper.countByTargetMemberId(request.getTargetMemberId());

        if (reportCount >= 10 && targetMember.getStatus() == MemberStatus.ACTIVE) {
            processMemberDeactivation(request.getTargetMemberId());
        }
    }

    private void processMemberDeactivation(Long targetId) {
        log.info("신고 10회 누적 유저({}) 정지 프로세스 시작", targetId);

        // 유저 상태를 INACTIVE로 변경
        memberMapper.updateMemberStatus(targetId, MemberStatus.INACTIVE);
        eventPublisher.publishEvent(new MemberStatusUpdateEvent(targetId,MemberStatus.INACTIVE));

        log.info("유저({}) 정지 및 경매 취소/본인 환불 처리 완료", targetId);
    }
}