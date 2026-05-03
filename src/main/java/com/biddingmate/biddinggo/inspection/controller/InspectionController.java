package com.biddingmate.biddinggo.inspection.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.inspection.dto.CreateInspectionRequest;
import com.biddingmate.biddinggo.inspection.dto.CreateInspectionResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionListRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionListResponse;
import com.biddingmate.biddinggo.inspection.dto.UpdateInspectionShippingRequest;
import com.biddingmate.biddinggo.inspection.service.InspectionApplicationService;
import com.biddingmate.biddinggo.inspection.service.InspectionService;
import com.biddingmate.biddinggo.inspection.dto.InspectionDetailResponse;
import com.biddingmate.biddinggo.inspection.service.InspectionQueryService;
import com.biddingmate.biddinggo.member.model.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
@Tag(name = "Inspection", description = "상품 검수 등록 API")
public class InspectionController {
    private final InspectionApplicationService inspectionApplicationService;
    private final InspectionService inspectionService;
    private final InspectionQueryService inspectionQueryService;

    @GetMapping("")
    @Operation(summary = "검수물품 목록 조회", description = "회원별 검수물품 목록을 조회하고, 선택한 검수 상태로 필터링합니다.")
    public ResponseEntity<ApiResponse<PageResponse<InspectionListResponse>>> getInspectionList(
            @Valid InspectionListRequest request,
            @AuthenticationPrincipal Member member) {

        PageResponse<InspectionListResponse> result = inspectionQueryService.getInspectionList(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "검수물품 목록 조회 완료", result);
    }

    @GetMapping("/{inspectionId}")
    @Operation(summary = "검수 아이템 상세 조회", description = "검수 정보, 상품 정보, 카테고리, 이미지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<InspectionDetailResponse>> getInspectionDetail(
            @PathVariable Long inspectionId) {

        InspectionDetailResponse result = inspectionQueryService.getInspectionDetail(inspectionId);

        return ApiResponse.of(HttpStatus.OK, null, "검수 아이템 상세 조회 완료", result);
    }

    @PostMapping("")
    @Operation(summary = "상품 검수 등록", description = "검수 대상 상품, 이미지, 검수 정보를 함께 등록합니다.")
    public ResponseEntity<ApiResponse<CreateInspectionResponse>> createInspection(
            @Valid @RequestBody CreateInspectionRequest request,
            @AuthenticationPrincipal Member member) {

        Long inspectionId = inspectionApplicationService.createInspection(request, member.getId());

        CreateInspectionResponse result = CreateInspectionResponse.builder()
                .inspectionId(inspectionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "상품 검수 등록 완료", result);
    }

    @PatchMapping("/{inspectionId}/shipping")
    @Operation(summary = "검수 배송 정보 등록", description = "검수에 택배사와 송장 번호를 등록합니다.")
    public ResponseEntity<ApiResponse<CreateInspectionResponse>> updateShippingInfo(
            @PathVariable Long inspectionId,
            @Valid @RequestBody UpdateInspectionShippingRequest request) {

        inspectionService.updateShippingInfo(inspectionId, request);

        CreateInspectionResponse result = CreateInspectionResponse.builder()
                .inspectionId(inspectionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "검수 배송 정보 등록 완료", result);
    }
}
