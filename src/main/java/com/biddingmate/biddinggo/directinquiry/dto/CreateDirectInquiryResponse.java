package com.biddingmate.biddinggo.directinquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "1대1 문의 등록 응답 DTO")
public class CreateDirectInquiryResponse {
    @Schema(description = "문의 ID", example = "1")
    private Long id;
    @Schema(description = "작성자 ID", example = "10")
    private Long writerId;
    @Schema(description = "문의 카테고리", example = "배송")
    private String category;
    @Schema(description = "문의 내용", example = "배송은 언제 시작되나요?")
    private String content;
    @Schema(description = "작성 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;
}
