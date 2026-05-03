package com.biddingmate.biddinggo.notice.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notice.dto.CreateNoticeRequest;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;
import com.biddingmate.biddinggo.notice.dto.UpdateNoticeRequest;
import com.biddingmate.biddinggo.notice.service.AdminNoticeService;
import com.biddingmate.biddinggo.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins/notices")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin-Notice", description = "공지사항")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;
    private final NoticeService noticeService;

    @GetMapping("")
    @Operation(summary = "관리자 공지사항 목록 조회")
    public ResponseEntity<ApiResponse<PageResponse<NoticeResponse>>> findAllNotices (BasePageRequest request) {
        PageResponse<NoticeResponse> result = noticeService.findAllNotices(request);
        return ApiResponse.of(HttpStatus.OK, null, "관리자 공지사항 목록 조회 성공", result);
    }


    @PostMapping
    @Operation(summary = "공지사항 등록")
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody CreateNoticeRequest request
    ) {

        NoticeResponse response = adminNoticeService.create(request);

        return ApiResponse.of(HttpStatus.CREATED, null, "공지사항 등록 완료", response);
    }

    @PutMapping("/{noticeId}")
    @Operation(summary = "공지사항 수정")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice (
            @PathVariable Long noticeId,
            @Valid @RequestBody UpdateNoticeRequest request
    ) {

        NoticeResponse response = adminNoticeService.update(noticeId, request);

        return ApiResponse.of(HttpStatus.OK, null, "공지사항 수정 완료", response);
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "공지사항 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteNotice (
            @PathVariable Long noticeId) {

        adminNoticeService.delete(noticeId);

        return ApiResponse.<Void>of(HttpStatus.OK, null, "공지 사항 삭제 완료", null);
    }



}
