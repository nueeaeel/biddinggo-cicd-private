package com.biddingmate.biddinggo.inspection.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListRequest;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionDetailResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionListRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionListResponse;
import com.biddingmate.biddinggo.inspection.mapper.InspectionMapper;
import com.biddingmate.biddinggo.item.mapper.ItemImageMapper;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 검수 상세 조회 구현체.
 * 본문 정보와 이미지 목록을 분리 조회한 뒤 하나의 응답 DTO로 조합한다.
 */
@Service
@RequiredArgsConstructor
public class InspectionQueryServiceImpl implements InspectionQueryService {
    private final InspectionMapper inspectionMapper;
    private final ItemImageMapper itemImageMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InspectionListResponse> getInspectionList(InspectionListRequest request, Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new CustomException(ErrorType.INVALID_INSPECTION_LIST_REQUEST);
        }

        String order = request.getOrder();
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }

        ItemInspectionStatus status = parseInspectionStatus(request.getStatus());
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());

        List<InspectionListResponse> list = inspectionMapper.findInspectionList(rowBounds, memberId, status);
        int count = inspectionMapper.countInspectionList(memberId, status);

        return PageResponse.of(list, request.getPage(), request.getSize(), count);
    }

    private ItemInspectionStatus parseInspectionStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return ItemInspectionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new CustomException(ErrorType.INVALID_INSPECTION_LIST_STATUS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * 검수 상세 조회 메인 로직.
     * 1) 검수/상품/카테고리 본문 조회
     * 2) item_image 목록 조회
     * 3) 이미지 목록을 응답 객체에 조합
     */
    public InspectionDetailResponse getInspectionDetail(Long inspectionId) {
        if (inspectionId == null || inspectionId <= 0) {
            throw new CustomException(ErrorType.BAD_REQUEST);
        }

        InspectionDetailResponse detail = inspectionMapper.findDetailById(inspectionId);

        if (detail == null) {
            throw new CustomException(ErrorType.INSPECTION_NOT_FOUND);
        }

        List<InspectionDetailResponse.Image> images = itemImageMapper.findInspectionDetailImagesByItemId(detail.getItem().getItemId());
        detail.getItem().setImages(images);

        return detail;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminInspectionListResponse> findAllWithFilter(AdminInspectionListRequest request) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());

        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }

        String sortOrder = order.toUpperCase();

        List<AdminInspectionListResponse> content =
                inspectionMapper.findAllWithFilter(request, rowBounds, sortOrder);

        long totalCount =
                inspectionMapper.countWithFilter(request);

        return PageResponse.of(content, request.getPage(), request.getSize(), totalCount);
    }
}
