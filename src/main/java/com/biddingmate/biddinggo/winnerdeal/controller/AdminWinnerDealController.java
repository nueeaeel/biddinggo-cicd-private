package com.biddingmate.biddinggo.winnerdeal.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealTrackingNumberRequest;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealQueryService;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealService;
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
@RequestMapping("/api/v1/admins/winner-deals")
@Tag(name = "Admin-Winner-Deal", description = "관리자 거래 관리 API")
public class AdminWinnerDealController {
    private final WinnerDealQueryService winnerDealQueryService;
    private final WinnerDealService winnerDealService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자 거래 내역 조회", description = "관리자가 거래 번호와 상태 조건으로 거래 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AdminWinnerDealListResponse>>> findAdminWinnerDealHistory(@Valid AdminWinnerDealListRequest request) {
        PageResponse<AdminWinnerDealListResponse> result = winnerDealQueryService.findAdminWinnerDealHistory(request);

        return ApiResponse.of(HttpStatus.OK, null, "관리자 거래 내역 조회에 성공했습니다.", result);
    }
    @GetMapping("/{winnerDealId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자 거래 상세 조회", description = "관리자가 거래 상세 정보와 구매자/판매자/배송 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AdminWinnerDealDetailResponse>> findAdminWinnerDealDetail(@Parameter(description = "거래 ID", example = "1") @PathVariable Long winnerDealId) {
        AdminWinnerDealDetailResponse result = winnerDealQueryService.findAdminWinnerDealDetail(winnerDealId);

        return ApiResponse.of(HttpStatus.OK, null, "관리자 거래 상세 조회에 성공했습니다.", result);
    }

    @PatchMapping("/{winnerDealId}/tracking-number")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자 검수 물품 운송장 등록", description = "관리자가 검수 물품 거래에 한해 운송장 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> registerTrackingNumber(@Parameter(description = "거래 ID", example = "1") @PathVariable Long winnerDealId,
                                                                    @Valid @RequestBody WinnerDealTrackingNumberRequest request) {
        winnerDealService.registerTrackingNumberByAdmin(winnerDealId, request);

        return ApiResponse.of(HttpStatus.OK, null, "관리자 운송장 등록에 성공했습니다.", null);
    }
}
