package com.biddingmate.biddinggo.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNoticeRequest {

    @NotBlank(message = "제목은 필수 입니다.")
    @Size(max = 20, message = "공지 제목은 20자 이하여야 합니다.")
    @Schema(example = "공지 title-수정")
    private String title;

    @NotBlank(message = "공지 내용은 필수 입니다.")
    @Schema(example = "공지 content-수정")
    private String content;
}
