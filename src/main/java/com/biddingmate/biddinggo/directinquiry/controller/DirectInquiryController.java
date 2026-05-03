package com.biddingmate.biddinggo.directinquiry.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/direct-inquiries")
@Tag(name = "DirectInquiry", description = "1대1 문의 API")
public class DirectInquiryController {
    private final DirectInquiryService directInquiryService;

    @PostMapping("")
    @Operation(summary = "1대1 문의 등록", description = "1대1 문의를 등록합니다.")
    public ResponseEntity<ApiResponse<CreateDirectInquiryResponse>> createDirectInquiry(@RequestBody @Valid CreateDirectInquiryRequest request,
                                                                                        @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        CreateDirectInquiryResponse result = directInquiryService.createDirectInquiry(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "1대1 문의 성공", result);
    }

    @GetMapping("")
    @Operation(summary = "1대1 문의 목록 조회", description = "내 1대1 문의를 페이징 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<DirectInquiryView>>> findDirectInquiry(@Valid BasePageRequest request,
                                                                                          @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        PageResponse<DirectInquiryView> result = directInquiryService.findDirectInquiry(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "1대1 문의 목록 조회 성공", result);
    }
}

