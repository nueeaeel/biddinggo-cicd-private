package com.biddingmate.biddinggo.item.service;

import com.biddingmate.biddinggo.item.dto.ItemImageCreateSource;

import java.util.List;

/**
 * item_image 테이블 저장 책임을 담당하는 서비스.
 */
public interface ItemImageService {
    /**
     * 업로드된 이미지 메타데이터를 item_image 테이블에 저장한다.
     */
    void createItemImages(Long itemId, List<? extends ItemImageCreateSource> images);
}
