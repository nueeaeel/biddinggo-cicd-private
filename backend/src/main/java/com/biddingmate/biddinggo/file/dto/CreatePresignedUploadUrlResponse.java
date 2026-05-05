package com.biddingmate.biddinggo.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "R2 presigned upload URL 발급 응답 DTO")
public class CreatePresignedUploadUrlResponse {
    @Schema(description = "PUT 업로드 URL")
    private String uploadUrl;

    @Schema(description = "R2에 저장될 임시 파일 key", example = "temp/items/2026/03/13/uuid.jpg")
    private String fileKey;

    @Schema(description = "업로드 후 접근할 공개 URL")
    private String publicUrl;

    @Schema(description = "업로드 HTTP 메서드", example = "PUT")
    private String method;

    @Schema(description = "업로드 URL 만료 시간(초)", example = "600")
    private Long expiresInSeconds;
}
