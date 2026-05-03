package com.biddingmate.biddinggo.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateNoticeRequest {

    // update,crate dto 필드가 동일해서 합치려다가 스웨거 때문에 유지했습니다.
    @NotBlank(message = "제목은 필수 입니다.")
    @Size(max = 20, message = "공지 제목은 20자 이하여야 합니다.")
    @Schema(example = "공지 title")
    private String title;

    @NotBlank(message = "공지 내용은 필수 입니다.")
    @Schema(example = "공지 content")
    private String content;
}
