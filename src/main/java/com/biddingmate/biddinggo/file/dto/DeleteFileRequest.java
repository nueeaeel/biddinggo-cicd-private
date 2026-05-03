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
@Schema(description = "R2 임시 파일 삭제 요청 DTO")
public class DeleteFileRequest {
    @Schema(description = "삭제할 R2 임시 파일 key", example = "temp/items/2026/03/13/uuid.jpg")
    @NotBlank(message = "파일 key는 필수입니다.")
    private String fileKey;
}
