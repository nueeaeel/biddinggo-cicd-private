package com.biddingmate.biddinggo.winnerdeal.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealShippingAddressRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealTrackingNumberRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryResponse;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealQueryService;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/winner-deals")
@Tag(name = "Winner-Deal", description = "낙찰 거래 관리 API")
public class WinnerDealController {
    private final WinnerDealQueryService winnerDealQueryService;
    private final WinnerDealService winnerDealService;

    @GetMapping("/purchases")
    @Operation(summary = "구매 내역 조회", description = "로그인한 사용자의 낙찰 거래 구매 내역을 상태별 필터와 함께 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<WinnerDealHistoryResponse>>> findPurchaseHistory(@Valid WinnerDealHistoryRequest request,
                                                                                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        PageResponse<WinnerDealHistoryResponse> result = winnerDealQueryService.findPurchaseHistory(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "구매 내역 조회에 성공했습니다.", result);
    }

    @GetMapping("/sales")
    @Operation(summary = "판매 내역 조회", description = "로그인한 사용자의 낙찰 거래 판매 내역을 상태별 필터와 함께 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<WinnerDealHistoryResponse>>> findSaleHistory(@Valid WinnerDealHistoryRequest request,
                                                                                                @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        PageResponse<WinnerDealHistoryResponse> result = winnerDealQueryService.findSaleHistory(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "판매 내역 조회에 성공했습니다.", result);
    }

    @GetMapping("/{winnerDealId}")
    @Operation(summary = "거래 내역 상세 조회", description = "로그인한 사용자의 낙찰 거래 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<WinnerDealDetailResponse>> findWinnerDealDetail(@Parameter(description = "낙찰 ID", example = "1") @PathVariable Long winnerDealId,
                                                                                      @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        WinnerDealDetailResponse result = winnerDealQueryService.findWinnerDealDetail(winnerDealId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "낙찰 거래 상세 조회에 성공했습니다.", result);
    }

    @PatchMapping("/{winnerDealId}/shipping-address")
    @Operation(summary = "낙찰 거래 배송지 등록", description = "구매자가 낙찰 거래에 배송지 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> registerShippingAddress(@Parameter(description = "낙찰 거래 ID", example = "1") @PathVariable Long winnerDealId,
                                                                     @Valid @RequestBody WinnerDealShippingAddressRequest request,
                                                                     @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        winnerDealService.registerShippingAddress(winnerDealId, member.getId(), request);

        return ApiResponse.of(HttpStatus.OK, null, "낙찰 거래 배송지 정보 등록에 성공했습니다.", null);
    }
    @PatchMapping("/{winnerDealId}/tracking-number")
    @Operation(summary = "낙찰 거래 운송장 등록", description = "판매자가 낙찰 거래에 운송장 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> registerTrackingNumber(@Parameter(description = "낙찰 거래 ID", example = "1") @PathVariable Long winnerDealId,
                                                                    @Valid @RequestBody WinnerDealTrackingNumberRequest request,
                                                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        winnerDealService.registerTrackingNumber(winnerDealId, member.getId(), request);

        return ApiResponse.of(HttpStatus.OK, null, "낙찰 거래 운송장 정보 등록에 성공했습니다.", null);
    }
    @PatchMapping("/{winnerDealId}/confirm")
    @Operation(summary = "낙찰 거래 구매확정", description = "구매자가 낙찰 거래를 구매확정 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> confirmPurchase(@Parameter(description = "낙찰 거래 ID", example = "1") @PathVariable Long winnerDealId,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        winnerDealService.confirmPurchase(winnerDealId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "낙찰 거래 구매확정에 성공했습니다.", null);
    }
}
