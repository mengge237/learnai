package com.learnai.dto.learning;

/**
 * 分类管理列表项（含停用分类与资源数）
 */
public record CategoryAdminDto(
        Long id,
        String name,
        String description,
        Long parentId,
        Integer sortOrder,
        Boolean isActive,
        long resourceCount
) {
}
