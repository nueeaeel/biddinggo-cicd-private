package com.biddingmate.biddinggo.inspection.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListRequest;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionProcessRequest;
import com.biddingmate.biddinggo.inspection.service.InspectionApplicationService;
import com.biddingmate.biddinggo.inspection.service.InspectionQueryService;
import com.biddingmate.biddinggo.inspection.service.InspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins/inspections")
@Tag(name = "Admin-Inspection", description = "관리자용 검수 관리 API")
public class AdminInspectionController {
    private final InspectionQueryService inspectionQueryService;
    private final InspectionService inspectionService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 검수 신청 목록 조회", description = "관리자는 모든 검수 신청 목록을 조건별로 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AdminInspectionListResponse>>> findAllInspection(AdminInspectionListRequest request) {
        PageResponse<AdminInspectionListResponse> result = inspectionQueryService.findAllWithFilter(request);

        return ApiResponse.of(HttpStatus.OK, null, "관리자 검수 요청 물품 조회 성공", result);
    }

    @PatchMapping("/{inspectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 검수 처리", description = "관리자가 검수 신청을 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> processInspection(@Parameter(description = "검수 ID", example = "1") @PathVariable Long inspectionId,
                                                               @Valid @RequestBody InspectionProcessRequest request) {
        inspectionService.processInspection(inspectionId, request);

        return ApiResponse.of(HttpStatus.OK, null, "관리자 검수 처리 성공", null);
    }
}
