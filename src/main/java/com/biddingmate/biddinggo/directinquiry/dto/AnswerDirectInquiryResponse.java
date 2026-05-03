package com.biddingmate.biddinggo.directinquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "1대1 문의 답변 등록 응답 DTO")
public class AnswerDirectInquiryResponse {
    @Schema(description = "문의 ID", example = "1")
    private Long id;
    @Schema(description = "관리자 ID", example = "2")
    private Long adminId;
    @Schema(description = "답변 내용", example = "금일 출고 예정입니다.")
    private String answer;
    @Schema(description = "답변 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime answeredAt;
}
