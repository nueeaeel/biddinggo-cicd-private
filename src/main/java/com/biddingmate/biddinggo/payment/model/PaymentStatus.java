package com.biddingmate.biddinggo.payment.model;

public enum PaymentStatus {
    WAITING_FOR_DEPOSIT,
    DONE,
    CANCELED,
    PARTIAL_CANCELED,
    ABORTED,
    EXPIRED
}
