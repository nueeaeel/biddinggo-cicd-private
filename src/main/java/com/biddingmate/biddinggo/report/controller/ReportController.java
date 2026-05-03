package com.biddingmate.biddinggo.report.controller;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.report.dto.ReportCreateRequest;
import com.biddingmate.biddinggo.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "신고 관리 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReport(
            @Valid @RequestBody ReportCreateRequest request,
            @AuthenticationPrincipal Member member
    ) {
        if (member == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED);
        }

        reportService.processReport(request,member.getId());
        return ApiResponse.<Void>of(
                HttpStatus.OK,
                null,
                "신고가 정상적으로 등록되었습니다.",
                null
        );
    }
}