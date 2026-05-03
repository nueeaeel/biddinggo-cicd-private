package com.biddingmate.biddinggo.notice.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notice.dto.NoticeResponse;
import com.biddingmate.biddinggo.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
@Tag(name = "User-Notice", description = "유저 공지사항")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("")
    @Operation(summary = "유저 공지사항 조회")
    public ResponseEntity<ApiResponse<PageResponse<NoticeResponse>>> findNotices(BasePageRequest request) {
        PageResponse<NoticeResponse> result = noticeService.findActiveNotices(request);
        return ApiResponse.of(HttpStatus.OK, null, "공지사항 목록 조회 성공", result);
    }
}
