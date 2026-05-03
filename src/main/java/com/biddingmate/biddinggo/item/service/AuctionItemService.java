package com.biddingmate.biddinggo.item.service;

import com.biddingmate.biddinggo.item.dto.AuctionItemCreateSource;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.item.model.AuctionItemStatus;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;

import java.util.List;

/**
 * auction_item 테이블 저장 책임을 담당하는 서비스.
 */
public interface AuctionItemService {
    /**
     * 경매 등록용 auction_item을 생성한다.
     *
     * <p>{@code auction_item.inspection_status}는 이 단계에서 명시하지 않고,
     * DB 기본값({@code NONE})을 사용한다.</p>
     */
    Long createAuctionItem(AuctionItemCreateSource item, Long sellerId);

    /**
     * 검수 등록용 auction_item을 생성한다.
     *
     * <p>검수 등록은 일반 경매 등록과 달리,
     * {@code auction_item.status = PENDING},
     * {@code auction_item.inspection_status = PENDING}을 명시 저장한다.</p>
     */
    Long createInspectionItem(AuctionItemCreateSource item, Long sellerId);

    /**
     * 검수 완료된 기존 상품이 경매 등록 가능한 상태인지 검증하고 반환한다.
     */
    AuctionItem getAuctionableInspectionItem(Long itemId, Long sellerId);

    /**
     * itemId를 기준으로 상품 정보를 조회한다.
     */
    AuctionItem getAuctionItem(Long itemId);

    /**
     * 상품 상태를 조건부로 변경한다.
     *
     * <p>범용 상태 변경 도구이며,
     * 실제 유스케이스에서는 {@code markAsOnAuction()}처럼 의미가 드러나는 전이 메서드로 감싸서 사용하는 것을 권장한다.</p>
     */
    void changeStatus(Long itemId, AuctionItemStatus newStatus, AuctionItemStatus currentStatus, ItemInspectionStatus currentInspectionStatus);

    /**
     * 검수 완료 상품을 경매 진행 상태로 전이한다.
     *
     * <p>애플리케이션 서비스가 상태 enum 조합을 직접 알지 않도록,
     * 경매 등록 유스케이스에서는 이 메서드를 통해 상태 전이를 수행한다.</p>
     */
    void markAsOnAuction(Long itemId);

    // 강제 취소된 물품들 상태 변경
    void cancelItemsByAuctionIds(List<Long> auctionIds);
}
