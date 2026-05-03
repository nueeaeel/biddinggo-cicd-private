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
public class VirtualAccount {
    private Long id;
    private Long paymentId;
    private String bankCode;
    private String bankAccount;
    private String accountHolderName;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
