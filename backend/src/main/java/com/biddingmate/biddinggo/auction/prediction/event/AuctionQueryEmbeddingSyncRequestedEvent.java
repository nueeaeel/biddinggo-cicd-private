package com.biddingmate.biddinggo.auction.prediction.event;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingSyncTrigger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 경매 등록/수정이 끝난 뒤 조회용 임베딩 동기화가 필요함을 알리는 이벤트.
 * 트랜잭션 안에서 외부 저장소를 직접 호출하지 않기 위해 필요한 원본 텍스트 정보를 함께 담아 전달한다.
 */
@Getter
@AllArgsConstructor
@Builder
public class AuctionQueryEmbeddingSyncRequestedEvent {
    private Long auctionId;
    private Long itemId;
    private Long categoryId;
    private String brand;
    private String name;
    private String quality;
    private String description;
    private AuctionEmbeddingSyncTrigger trigger;
}
