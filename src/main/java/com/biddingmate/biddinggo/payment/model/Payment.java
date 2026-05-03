package com.biddingmate.biddinggo.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    private Long id;
    private Long memberId;
    private String orderId;
    private String paymentKey;
    private String paymentMethod;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
