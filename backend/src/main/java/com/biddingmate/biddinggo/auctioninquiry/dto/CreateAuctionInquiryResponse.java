package com.biddingmate.biddinggo.auctioninquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CreateAuctionInquiryResponse {

    @Schema(description = "문의 ID", example = "1")
    private Long id;

    @Schema(description = "경매 ID", example = "10")
    private Long auctionId;

    @Schema(description = "작성자 ID", example = "1")
    private Long writerId;

    private String title;

    @Schema(description = "문의 내용", example = "상품 상태가 어떤가요?")
    private String content;

    @Schema(description = "생성 시간", example = "2026-03-22T12:00:00")
    private LocalDateTime createdAt;
}