package com.biddingmate.biddinggo.item.service;

import com.biddingmate.biddinggo.item.dto.CategoryListResponse;
import com.biddingmate.biddinggo.item.dto.CategoryResponse;
import com.biddingmate.biddinggo.item.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse getCategories() {
        List<CategoryResponse> categories = categoryMapper.findAllCategories();

        return CategoryListResponse.builder()
                .categories(categories)
                .build();
    }
}
