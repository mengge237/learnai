package com.learnai.dto.learning;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 替换路径资源序列请求（管理员，按顺序）
 */
public record PathResourcesRequest(
        @NotEmpty(message = "资源列表不能为空")
        List<Long> resourceIds
) {
}
