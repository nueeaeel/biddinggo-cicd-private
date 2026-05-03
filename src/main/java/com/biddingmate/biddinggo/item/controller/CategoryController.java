package com.biddingmate.biddinggo.item.controller;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.item.dto.CategoryListResponse;
import com.biddingmate.biddinggo.item.service.CategoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "카테고리 조회 API")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;

    @GetMapping("")
    @Operation(summary = "카테고리 목록 조회", description = "전체 카테고리 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories() {
        CategoryListResponse result = categoryQueryService.getCategories();

        return ApiResponse.of(HttpStatus.OK, null, "카테고리 조회 완료", result);
    }
}
