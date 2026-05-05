package com.biddingmate.biddinggo.payment.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.payment.dto.CreateVirtualAccountRequest;
import com.biddingmate.biddinggo.payment.dto.CreateVirtualAccountResponse;
import com.biddingmate.biddinggo.payment.dto.GetVirtualAccountResponse;
import com.biddingmate.biddinggo.payment.dto.TossDepositWebhook;
import com.biddingmate.biddinggo.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/virtual-accounts")
    @Operation(summary = "가상계좌 발급", description = "가상계좌를 발급합니다.")
    public ResponseEntity<ApiResponse<CreateVirtualAccountResponse>> createVirtualAccount(@RequestBody CreateVirtualAccountRequest request,
                                                                                          @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        CreateVirtualAccountResponse result = paymentService.createVirtualAccount(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "가상계좌 발급 성공", result);
    }

    @GetMapping("/virtual-accounts")
    @Operation(summary = "가상계좌 조회", description = "회원의 가상계좌 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<GetVirtualAccountResponse>>> getVirtualAccount(@Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        List<GetVirtualAccountResponse> result = paymentService.getVirtualAccount(member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "가상계좌 조회 성공", result);
    }

    @PostMapping("/virtual-accounts/deposit")
    @Operation(summary = "가상계좌 입금 웹훅", description = "토스 가상계좌 입금 웹훅을 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> handleTossDepositWebhook(@RequestBody TossDepositWebhook request) {
        // 1. 로그로 데이터 확인 (가장 중요!)
        System.out.println("토스 웹훅 도착!: \n" + request);

        // 2. 서비스 로직 호출 (입금 완료 처리 등)
        paymentService.processDeposit(request);

        // 3. 토스에게 성공적으로 받았음을 알림 (200 OK)
        return ApiResponse.of(HttpStatus.OK, null, "입금 및 포인트 충전 완료", null);
    }
}
