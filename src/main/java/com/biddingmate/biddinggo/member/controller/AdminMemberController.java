package com.biddingmate.biddinggo.member.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.dto.MemberListView;
import com.biddingmate.biddinggo.member.dto.MemberListViewRequest;
import com.biddingmate.biddinggo.member.dto.UpdateMemberStatusRequest;
import com.biddingmate.biddinggo.member.service.MemberService;
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
@RequestMapping("/api/v1/admins/members")
@Tag(name = "Admin-Member", description = "관리자용 회원 관리 API")
public class AdminMemberController {
    private final MemberService memberService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 회원 목록 조회", description = "관리자는 회원 목록을 조건별로 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<MemberListView>>> findAllMemberWithFilter(@Valid MemberListViewRequest request) {
        PageResponse<MemberListView> result = memberService.findAllMemberWithFilter(request);

        return ApiResponse.of(HttpStatus.OK, null, "관리자용 모든 사용자의 계정 조회 성공", result);
    }

    @PatchMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 회원 상태 변경", description = "관리자가 회원 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateMemberStatus(@Parameter(description = "회원 ID", example = "1") @PathVariable Long memberId,
                                                                @RequestBody @Valid UpdateMemberStatusRequest request) {
        memberService.updateMemberStatus(memberId, request);

        return ApiResponse.of(HttpStatus.OK, null, "해당 사용자의 상태 변경에 성공했습니다.", null);
    }
}
