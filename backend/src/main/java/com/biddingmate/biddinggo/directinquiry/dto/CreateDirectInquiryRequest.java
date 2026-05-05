package com.biddingmate.biddinggo.directinquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "1대1 문의 등록 요청 DTO")
public class CreateDirectInquiryRequest {
    @Schema(description = "문의 카테고리", example = "배송")
    @NotBlank(message = "문의 카테고리는 필수입니다.")
    private String category;

    @Schema(description = "문의 내용", example = "배송은 언제 시작되나요?")
    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;
}
