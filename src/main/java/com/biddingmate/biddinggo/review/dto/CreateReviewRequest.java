package com.biddingmate.biddinggo.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    @NotNull(message = "별점은 필수입니다.")
    @Min(1) @Max(5) // 별점 1~5점 제한
    private Integer rating;

    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    @Size(max = 500)
    private String content;

}
