package com.biddingmate.biddinggo.member.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.dto.MemberDashboardResponse;
import com.biddingmate.biddinggo.member.dto.MemberProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberProfileUpdateRequest;
import com.biddingmate.biddinggo.member.dto.MemberPurchaseItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResultResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesItemResponse;
import com.biddingmate.biddinggo.member.dto.MemberSellerProfileResponse;
import com.biddingmate.biddinggo.member.dto.MemberSalesAuctionResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "Member", description = "회원 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "마이페이지 조회", description = "로그인한 회원의 대시보드 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberDashboardResponse>> getDashboard(@AuthenticationPrincipal Member member) {
        MemberDashboardResponse result = memberService.getMyDashboard(member.getId());
        return ApiResponse.of(HttpStatus.OK, null, "회원 마이페이지 조회 성공", result);
    }

    @Operation(summary = "프로필 조회", description = "로그인한 회원의 프로필 정보를 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getProfile(@AuthenticationPrincipal Member member) {
        MemberProfileResponse result = memberService.getMyProfile(member.getId());
        return ApiResponse.of(HttpStatus.OK, null, "회원 프로필 조회 성공", result);
    }

    @Operation(summary = "프로필 수정", description = "로그인한 회원의 프로필 정보를 수정합니다.")
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal Member member,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 수정 요청 DTO",
                    required = true
            )
            @RequestBody MemberProfileUpdateRequest request
    ) {
        MemberProfileResponse result = memberService.updateMyProfile(member.getId(), request);
        return ApiResponse.of(HttpStatus.OK, null, "회원 프로필 수정 성공", result);
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 회원이 계정을 탈퇴합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@AuthenticationPrincipal Member member) {
        memberService.deleteMyAccount(member.getId());
        return ApiResponse.of(HttpStatus.OK, null, "회원 탈퇴 성공", null);
    }

    @Operation(
            summary = "경매관리",
            description = "로그인한 회원의 경매를 상태별(ALL, PENDING, ONGOING, SUCCESS, FAILED, CANCELLED)로 조회합니다."
    )
    @GetMapping("/me/sales/auctions")
    public ResponseEntity<ApiResponse<MemberSalesAuctionResultResponse>> getSellingItems(
            @AuthenticationPrincipal Member member,
            @Parameter(
                    description = "조회 타입(ALL, PENDING, ONGOING, SUCCESS, FAILED, CANCELLED)",
                    example = "ONGOING"
            )
            @RequestParam String type,
            @Valid BasePageRequest pageRequest
    ) {
        MemberSalesAuctionResultResponse result =
                memberService.getMySalesAuctions(member.getId(), type, pageRequest);

        return ApiResponse.of(HttpStatus.OK, null, "경매 관리 목록 조회 성공", result);
    }

    @Operation(summary = "판매자 프로필 조회", description = "판매자의 프로필 정보를 조회합니다.")
    @GetMapping("/{sellerId}")
    public ResponseEntity<ApiResponse<MemberSellerProfileResponse>> getSellerProfile(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId
    ) {
        MemberSellerProfileResponse result = memberService.getSellerProfile(sellerId);
        return ApiResponse.of(HttpStatus.OK, null, "판매자 정보 조회 성공", result);
    }
}