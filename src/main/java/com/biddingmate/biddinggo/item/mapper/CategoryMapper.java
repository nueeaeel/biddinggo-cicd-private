package com.biddingmate.biddinggo.item.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.item.dto.CategoryResponse;
import com.biddingmate.biddinggo.item.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends IMybatisCRUD<Category> {
    Category findById(Long id);

    List<CategoryResponse> findAllCategories();

    boolean existsChildByParentId(Long parentId);
}
