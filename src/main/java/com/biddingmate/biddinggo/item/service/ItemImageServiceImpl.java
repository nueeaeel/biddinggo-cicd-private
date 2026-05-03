package com.biddingmate.biddinggo.item.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.file.model.FileMetadata;
import com.biddingmate.biddinggo.file.service.FileService;
import com.biddingmate.biddinggo.item.dto.ItemImageCreateSource;
import com.biddingmate.biddinggo.item.mapper.ItemImageMapper;
import com.biddingmate.biddinggo.item.model.ItemImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * item_image 엔티티 생성만 담당하는 서비스 구현체.
 * 업로드된 임시 파일 key를 기준으로 R2 메타데이터를 조회한 뒤 저장한다.
 */
@Service
@RequiredArgsConstructor
public class ItemImageServiceImpl implements ItemImageService {
    private final FileService fileService;
    private final ItemImageMapper itemImageMapper;

    @Override
    /**
     * item_image 목록 저장 로직.
     * displayOrder 중복 여부를 먼저 확인하고,
     * 각 파일의 실제 R2 메타데이터를 조회해 DB에 저장한다.
     */
    public void createItemImages(Long itemId, List<? extends ItemImageCreateSource> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        if (itemId == null) {
            throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
        }

        validateDisplayOrders(images);

        for (ItemImageCreateSource image : images) {
            if (!fileService.isManagedFileKey(image.getFileKey())) {
                throw new CustomException(ErrorType.INVALID_AUCTION_CREATE_REQUEST);
            }

            FileMetadata fileMetadata = fileService.getFileMetadata(image.getFileKey());

            ItemImage itemImage = ItemImage.builder()
                    .itemId(itemId)
                    .url(fileService.buildPublicUrl(image.getFileKey()))
                    .displayOrder(image.getDisplayOrder())
                    .type(fileMetadata.getContentType())
                    .size(fileMetadata.getSize())
                    .createdAt(LocalDateTime.now())
                    .build();

            int imageInsertCount = itemImageMapper.insert(itemImage);

            if (imageInsertCount != 1 || itemImage.getId() == null) {
                throw new CustomException(ErrorType.ITEM_IMAGE_SAVE_FAILED);
            }
        }
    }

    /**
     * 같은 item 안에서 이미지 노출 순서가 중복되지 않도록 검증한다.
     */
    private void validateDisplayOrders(List<? extends ItemImageCreateSource> images) {
        Set<Integer> displayOrders = new HashSet<>();

        for (ItemImageCreateSource image : images) {
            if (!displayOrders.add(image.getDisplayOrder())) {
                throw new CustomException(ErrorType.DUPLICATE_ITEM_IMAGE_DISPLAY_ORDER);
            }
        }
    }
}
