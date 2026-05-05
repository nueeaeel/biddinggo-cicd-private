package com.biddingmate.biddinggo.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "R2 임시 파일 삭제 응답 DTO")
public class DeleteFileResponse {
    @Schema(description = "삭제된 임시 파일 key", example = "temp/items/2026/03/13/uuid.jpg")
    private String fileKey;
}
