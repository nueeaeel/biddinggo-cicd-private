package com.biddingmate.biddinggo.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 목록 조회 응답 DTO")
public class CategoryListResponse {
    @Schema(description = "카테고리 목록")
    private List<CategoryResponse> categories;
}
