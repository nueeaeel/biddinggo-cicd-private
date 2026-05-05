package com.biddingmate.biddinggo.auction.prediction.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 경매 낙찰이 완료된 뒤 예측용 과거 낙찰 reference 동기화가 필요함을 알리는 이벤트.
 */
@Getter
@AllArgsConstructor
@Builder
public class AuctionPriceReferenceSyncRequestedEvent {
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private String brand;
    private String name;
    private String quality;
    private String description;
    private Long winnerPrice;
    private LocalDateTime completedAt;
}
