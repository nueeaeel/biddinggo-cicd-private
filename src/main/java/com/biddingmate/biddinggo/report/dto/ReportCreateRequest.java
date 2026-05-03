package com.biddingmate.biddinggo.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateRequest {
    @NotNull(message = "경매 ID는 필수입니다.")
    private Long auctionId;
    @NotNull(message = "신고 대상 유저 ID는 필수입니다.")
    private Long targetMemberId;
    @NotBlank(message = "신고 사유를 입력해 주세요.")
    private String reason;
}