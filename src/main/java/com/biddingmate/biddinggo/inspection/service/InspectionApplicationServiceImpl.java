package com.biddingmate.biddinggo.inspection.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.file.service.FileService;
import com.biddingmate.biddinggo.inspection.dto.CreateInspectionRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionProcessRequest;
import com.biddingmate.biddinggo.inspection.model.Inspection;
import com.biddingmate.biddinggo.inspection.model.InspectionStatus;
import com.biddingmate.biddinggo.item.model.AuctionItemStatus;
import com.biddingmate.biddinggo.item.service.AuctionItemService;
import com.biddingmate.biddinggo.item.service.ItemImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 검수 등록 전체 흐름을 조율하는 애플리케이션 서비스.
 * 트랜잭션 경계는 이 클래스에서만 관리한다.
 * auction_item, item_image, inspection 생성을 하나의 유스케이스로 묶는다.
 */
@Service
@RequiredArgsConstructor
public class InspectionApplicationServiceImpl implements InspectionApplicationService {
    private final AuctionItemService auctionItemService;
    private final ItemImageService itemImageService;
    private final InspectionService inspectionService;
    private final FileService fileService;

    @Override
    @Transactional
    /**
     * 검수 등록 메인 플로우.
     * 업로드된 임시 파일 목록을 먼저 확보해두고,
     * 중간 실패 시 R2 cleanup까지 함께 처리한다.
     */
    public Long createInspection(CreateInspectionRequest request, Long memberId) {
        List<String> uploadedFileKeys = extractUploadedFileKeys(request);

        try {
            // 1. 검수 대상 상품을 먼저 생성하여 itemId를 확보한다.
            Long itemId = auctionItemService.createInspectionItem(request.getItem(), memberId);

            // 2. 업로드된 이미지 메타데이터를 item_image에 저장한다.
            itemImageService.createItemImages(itemId, request.getItem().getImages());

            // 3. 생성된 itemId로 inspection 레코드를 생성한다.
            return inspectionService.createInspection(request, itemId);
        } catch (RuntimeException exception) {
            // DB 저장 중 예외가 발생하면 업로드된 임시 파일도 함께 정리한다.
            fileService.deleteFiles(uploadedFileKeys);
            throw exception;
        }
    }

    /**
     * 등록 요청에 포함된 임시 업로드 파일 key 목록을 추출한다.
     * 실패 시 cleanup 대상으로 사용한다.
     */
    private List<String> extractUploadedFileKeys(CreateInspectionRequest request) {
        if (request == null || request.getItem() == null || request.getItem().getImages() == null) {
            return List.of();
        }

        return request.getItem().getImages().stream()
                .filter(image -> image != null && image.getFileKey() != null && !image.getFileKey().isBlank())
                .map(CreateInspectionRequest.Image::getFileKey)
                .toList();
    }
}
