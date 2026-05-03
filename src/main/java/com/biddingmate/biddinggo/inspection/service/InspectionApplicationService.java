package com.biddingmate.biddinggo.inspection.service;

import com.biddingmate.biddinggo.inspection.dto.CreateInspectionRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionProcessRequest;

/**
 * 상품 검수 등록 유스케이스를 조합하는 상위 서비스.
 * 컨트롤러는 이 서비스를 통해 검수 등록을 시작한다.
 */
public interface InspectionApplicationService {
    /**
     * 검수 대상 상품 생성, 이미지 저장, inspection 생성을 하나의 흐름으로 처리한다.
     */
    Long createInspection(CreateInspectionRequest request, Long memberId);
}
