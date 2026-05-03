package com.biddingmate.biddinggo.point.model;

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
public class PointHistory {
    private Long id;
    private Long memberId;
    private Long paymentId;
    private Long bidId;
    private PointHistoryType type;
    private Long amount;
    private LocalDateTime createdAt;
}
