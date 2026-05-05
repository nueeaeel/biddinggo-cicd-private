package com.biddingmate.biddinggo.inspection.model;

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
public class Inspection {
    private Long id;
    private Long itemId;
    private LocalDateTime receivedAt;
    private InspectionStatus status;
    private LocalDateTime completedAt;
    private String failureReason;
    private String carrier;
    private String trackingNumber;
    private LocalDateTime createdAt;
}
