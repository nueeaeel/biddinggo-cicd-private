package com.biddingmate.biddinggo.payment.service;

import com.biddingmate.biddinggo.payment.dto.CreateVirtualAccountRequest;
import com.biddingmate.biddinggo.payment.dto.CreateVirtualAccountResponse;
import com.biddingmate.biddinggo.payment.dto.GetVirtualAccountResponse;
import com.biddingmate.biddinggo.payment.dto.TossDepositWebhook;

import java.util.List;

public interface PaymentService {
    CreateVirtualAccountResponse createVirtualAccount(CreateVirtualAccountRequest request, Long memberId);
    List<GetVirtualAccountResponse> getVirtualAccount(Long id);
    void processDeposit(TossDepositWebhook request);
}
