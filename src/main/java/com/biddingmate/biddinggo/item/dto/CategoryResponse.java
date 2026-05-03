package com.biddingmate.biddinggo.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "카테고리 조회 응답 DTO")
public class CategoryResponse {
    @Schema(description = "카테고리 ID", example = "111")
    private Long id;

    @Schema(description = "부모 카테고리 ID", example = "11", nullable = true)
    private Long parentId;

    @Schema(description = "카테고리명", example = "게이밍 노트북")
    private String name;

    @Schema(description = "카테고리 레벨", example = "3")
    private Integer level;

    @Schema(description = "하위 카테고리 존재 여부", example = "false")
    private Boolean hasChildren;

    @Schema(description = "선택 가능 여부", example = "true")
    private Boolean selectable;
}
