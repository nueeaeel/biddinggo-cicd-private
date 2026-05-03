package com.biddingmate.biddinggo.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "R2 presigned upload URL 발급 요청 DTO")
public class CreatePresignedUploadUrlRequest {
    @Schema(description = "원본 파일명", example = "main.jpg")
    @NotBlank(message = "원본 파일명은 필수입니다.")
    private String originalFilename;

    @Schema(description = "콘텐츠 타입", example = "image/jpeg")
    @NotBlank(message = "콘텐츠 타입은 필수입니다.")
    private String contentType;
}
