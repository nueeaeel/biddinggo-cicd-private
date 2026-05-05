package com.biddingmate.biddinggo.inspection.service;

import com.biddingmate.biddinggo.inspection.dto.CreateInspectionRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionProcessRequest;
import com.biddingmate.biddinggo.inspection.dto.UpdateInspectionShippingRequest;

/**
 * inspection 테이블 저장 책임을 담당하는 서비스.
 */
public interface InspectionService {
    /**
     * 이미 생성된 itemId를 기준으로 inspection 데이터를 저장한다.
     */
    Long createInspection(CreateInspectionRequest request, Long itemId);

    /**
     * 검수 배송 정보(택배사, 송장 번호)를 등록한다.
     */
    void updateShippingInfo(Long inspectionId, UpdateInspectionShippingRequest request);

    // 관리자 검수 요청 처리(승인/반려)
    void processInspection(Long inspectionId, InspectionProcessRequest request);
}
