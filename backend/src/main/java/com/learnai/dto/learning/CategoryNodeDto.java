package com.learnai.dto.learning;

import com.learnai.entity.ResourceCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类树节点（resourceCount = 该分类及全部子分类的公开资源数）
 */
public record CategoryNodeDto(
        Long id,
        String name,
        String description,
        Long parentId,
        Integer sortOrder,
        Integer resourceCount,
        List<CategoryNodeDto> children
) {
    public static CategoryNodeDto from(ResourceCategory c) {
        return new CategoryNodeDto(
                c.getCategoryId(), c.getCategoryName(), c.getDescription(),
                c.getParentCategoryId(), c.getSortOrder(), 0, new ArrayList<>());
    }
}
