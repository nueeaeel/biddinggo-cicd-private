package com.biddingmate.biddinggo.winnerdeal.scheduler;

import com.biddingmate.biddinggo.auction.mapper.AuctionMapper;
import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.auction.model.AuctionStatus;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionMapper auctionMapper;
    private final WinnerDealService winnerDealService;

    @Scheduled(cron = "*/5 * * * * *") // 5초 주기 설정
    public void executeClosing() {
        log.info("경매 마감 스케줄러 가동: {}", LocalDateTime.now());

        // 현재 진행 중인 경매 조회
        List<Auction> expiredAuctions = auctionMapper.findExpiredAuctions(
                LocalDateTime.now(),
                AuctionStatus.ON_GOING
        );

        for (Auction auction : expiredAuctions) {
            try {
                winnerDealService.processClosing(auction);
            } catch (CustomException e) {
                // 비즈니스 로직 예외 발생 시 에러 코드와 함께 상세 로그 기록
                log.error("경매 종료 비즈니스 에러 (ID: {}): [{}] {}",
                        auction.getId(), e.getErrorType().getErrorCode(), e.getMessage());
            } catch (Exception e) {
                // 그 외 예상치 못한 시스템 에러 처리
                log.error("경매 종료 시스템 에러 (ID: {}): {}",
                        auction.getId(), e.getMessage());
            }
        }
    }
}