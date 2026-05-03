package com.biddingmate.biddinggo.auction.service;

import com.biddingmate.biddinggo.auction.dto.CreateAuctionFromInspectionItemRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionRequest;
import com.biddingmate.biddinggo.auction.prediction.event.AuctionQueryEmbeddingSyncRequestedEvent;
import com.biddingmate.biddinggo.auction.prediction.model.AuctionEmbeddingSyncTrigger;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.file.service.FileService;
import com.biddingmate.biddinggo.item.dto.AuctionItemCreateSource;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.item.service.AuctionItemService;
import com.biddingmate.biddinggo.item.service.ItemImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 경매 등록 전체 흐름을 조율하는 애플리케이션 서비스.
 * 트랜잭션 경계는 이 클래스에서만 관리한다.
 * auction_item, item_image, auction 생성을 하나의 유스케이스로 묶는다.
 */
@Service
@RequiredArgsConstructor
public class AuctionApplicationServiceImpl implements AuctionApplicationService {
    private final AuctionItemService auctionItemService;
    private final ItemImageService itemImageService;
    private final AuctionService auctionService;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    /**
     * 경매 등록 메인 플로우.
     * 업로드된 임시 파일 목록을 먼저 확보해두고,
     * 중간 실패 시 R2 cleanup까지 함께 처리한다.
     */
    public Long createAuction(CreateAuctionRequest request, Long memberId) {
        List<String> uploadedFileKeys = extractUploadedFileKeys(request);

        try {
            validateRequest(request);

            // 1. auction_item 먼저 생성하여 itemId를 확보한다.
            Long itemId = auctionItemService.createAuctionItem(request.getItem(), memberId);

            // 2. 업로드된 이미지 메타데이터를 item_image에 저장한다.
            itemImageService.createItemImages(itemId, request.getItem().getImages());

            // 3. 생성된 itemId로 auction을 생성한다.
            Long auctionId = auctionService.createAuction(request, itemId, memberId);
            publishAuctionQueryEmbeddingSyncRequestedEvent(auctionId, itemId, request.getItem(), AuctionEmbeddingSyncTrigger.CREATED);
            return auctionId;
        } catch (RuntimeException exception) {
            fileService.deleteFiles(uploadedFileKeys);
            throw exception;
        }
    }

    @Override
    @Transactional
    /**
     * 검수 완료된 기존 상품 기반 경매 등록 메인 플로우.
     * 상품/이미지는 새로 생성하지 않고, 기존 auction_item의 상태만 검증한 뒤 auction을 생성한다.
     */
    public Long createAuctionFromInspectionItem(CreateAuctionFromInspectionItemRequest request, Long memberId) {
        validateRequest(request);

        // 1. 기존 상품을 조회하고, 실제 경매 등록 가능한 상태인지 검증한다.
        AuctionItem auctionItem = auctionItemService.getAuctionableInspectionItem(request.getItemId(), memberId);

        // 2. 검증이 끝난 기존 itemId로 auction만 생성한다.
        Long auctionId = auctionService.createAuction(request, memberId);

        // 3. 경매 생성이 완료되면 상품 상태를 경매 진행 중으로 전이한다.
        auctionItemService.markAsOnAuction(request.getItemId());
        publishAuctionQueryEmbeddingSyncRequestedEvent(auctionId, request.getItemId(), auctionItem, AuctionEmbeddingSyncTrigger.CREATED);

        return auctionId;
    }

    private void publishAuctionQueryEmbeddingSyncRequestedEvent(Long auctionId, Long itemId, AuctionItemCreateSource item, AuctionEmbeddingSyncTrigger trigger) {
        eventPublisher.publishEvent(AuctionQueryEmbeddingSyncRequestedEvent.builder()
                .auctionId(auctionId)
                .itemId(itemId)
                .categoryId(item.getCategoryId())
                .brand(item.getBrand())
                .name(item.getName())
                .quality(item.getQuality())
                .description(item.getDescription())
                .trigger(trigger)
                .build());
    }

    /**
     * DTO 기본 검증은 컨트롤러에서 처리하고,
     * 여기서는 비즈니스 규칙만 검증한다.
     */
    private void validateRequest(CreateAuctionRequest request) {
        if (!request.getAuction().getEndDate().isAfter(request.getAuction().getStartDate())) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }
    }

    /**
     * 검수 완료 상품 기반 경매 등록 요청의 비즈니스 규칙을 검증한다.
     */
    private void validateRequest(CreateAuctionFromInspectionItemRequest request) {
        if (!request.getAuction().getEndDate().isAfter(request.getAuction().getStartDate())) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }
    }

    /**
     * 등록 요청에 포함된 임시 업로드 파일 key 목록을 추출한다.
     * 실패 시 cleanup 대상으로 사용한다.
     */
    private List<String> extractUploadedFileKeys(CreateAuctionRequest request) {
        if (request == null || request.getItem() == null || request.getItem().getImages() == null) {
            return List.of();
        }

        return request.getItem().getImages().stream()
                .filter(image -> image != null && image.getFileKey() != null && !image.getFileKey().isBlank())
                .map(CreateAuctionRequest.Image::getFileKey)
                .toList();
    }
}
