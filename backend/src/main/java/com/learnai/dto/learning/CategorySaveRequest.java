package com.learnai.dto.learning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 分类新增 / 修改请求
 */
public record CategorySaveRequest(
        @NotBlank(message = "分类名称不能为空") @Size(max = 50) String name,
        @Size(max = 500) String description,
        Long parentId,
        @NotNull(message = "排序号不能为空") Integer sortOrder,
        @NotNull(message = "启用状态不能为空") Boolean isActive
) {
}
