package com.biddingmate.biddinggo.directinquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "1대1 문의 답변 등록 요청 DTO")
public class AnswerDirectInquiryRequest {
    @Schema(description = "답변 내용", example = "금일 출고 예정입니다.")
    @NotBlank(message = "답변 내용은 필수입니다.")
    private String answer;
}

