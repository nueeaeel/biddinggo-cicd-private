package com.biddingmate.biddinggo.directinquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "1대1 문의 목록 조회 DTO")
public class DirectInquiryView {
    @Schema(description = "문의 ID", example = "1")
    private Long id;
    @Schema(description = "작성자 닉네임", example = "biddingmate")
    private String nickname;
    @Schema(description = "문의 카테고리", example = "배송")
    private String category;
    @Schema(description = "문의 내용", example = "배송은 언제 시작되나요?")
    private String content;
    @Schema(description = "관리자 닉네임", example = "admin01")
    private String adminNickname;
    @Schema(description = "답변 내용", example = "금일 출고 예정입니다.")
    private String answer;
    @Schema(description = "답변 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime answeredAt;
    @Schema(description = "작성 일시", example = "2026-04-05T10:00:00")
    private LocalDateTime createdAt;

}

