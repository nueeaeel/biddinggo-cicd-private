package com.biddingmate.biddinggo.directinquiry.controller;


import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryResponse;
import com.biddingmate.biddinggo.directinquiry.dto.DirectInquiryView;
import com.biddingmate.biddinggo.directinquiry.service.DirectInquiryService;
import com.biddingmate.biddinggo.member.model.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins/direct-inquiries")
@Tag(name = "Admin-DirectInquiry", description = "관리자용 1대1 문의 관리 API")
public class AdminDirectInquiryController {
    private final DirectInquiryService directInquiryService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 1대1 문의 목록 조회", description = "관리자는 모든 사용자의 1대1 문의를 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<DirectInquiryView>>> findAllDirectInquiry(@Valid BasePageRequest request) {
        PageResponse<DirectInquiryView> result = directInquiryService.findAllDirectInquiry(request);

        return ApiResponse.of(HttpStatus.OK, null, "1대1 문의 목록 조회 성공", result);
    }

    @PatchMapping("/{inquiryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자용 1대1 문의 답변 등록", description = "관리자가 1대1 문의에 답변을 등록합니다.")
    public ResponseEntity<ApiResponse<AnswerDirectInquiryResponse>> answerDirectInquiry(@Parameter(description = "문의 ID", example = "1") @PathVariable Long inquiryId,
                                                                                        @RequestBody @Valid AnswerDirectInquiryRequest request,
                                                                                        @Parameter(hidden = true) @AuthenticationPrincipal Member admin) {
        AnswerDirectInquiryResponse result = directInquiryService.answerDirectInquiry(inquiryId, request, admin.getId());

        return ApiResponse.of(HttpStatus.OK, null, "1대1 문의 답변 등록 성공", result);
    }
}





