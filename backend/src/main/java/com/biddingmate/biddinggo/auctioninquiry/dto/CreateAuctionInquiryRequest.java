package com.biddingmate.biddinggo.auctioninquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAuctionInquiryRequest {

    @Schema(description = "경매 ID", example = "1", required = true)
    @NotNull(message = "경매 ID는 필수입니다.")
    private Long auctionId;

    @Schema(description = "문의 제목", example = "상품 상태가 어떤가요?", required = true) // 추가
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 50, message = "제목은 50자 이내로 입력해주세요.")
    private String title;

    @Schema(description = "문의 내용", example = "직거래 가능한가요?", required = true)
    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;

    private boolean secretYn;
}