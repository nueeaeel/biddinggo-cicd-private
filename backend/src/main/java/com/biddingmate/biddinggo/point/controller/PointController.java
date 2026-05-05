package com.biddingmate.biddinggo.point.controller;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.point.dto.ExchangePointRequest;
import com.biddingmate.biddinggo.point.dto.MyPointResponse;
import com.biddingmate.biddinggo.point.service.PointService;
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
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
@Tag(name = "Point", description = "포인트 관리 API")
public class PointController {
    private final PointService pointService;

    @GetMapping("/me")
    @Operation(summary = "내 포인트 조회", description = "내 포인트 및 포인트 내역을 페이징 조회합니다.")
    public ResponseEntity<ApiResponse<MyPointResponse>> findMyPointList(BasePageRequest request,
                                                                        @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        MyPointResponse result = pointService.findMyPointList(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "마이페이지 포인트 내역 조회에 성공했습니다.", result);
    }

    @PostMapping("/exchanges")
    @Operation(summary = "포인트 환전", description = "보유 포인트를 환전합니다.")
    public ResponseEntity<ApiResponse<Void>> exchangePoint(@RequestBody @Valid ExchangePointRequest request,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        pointService.exchangePoint(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "포인트 환전에 성공했습니다.", null);
    }
}
