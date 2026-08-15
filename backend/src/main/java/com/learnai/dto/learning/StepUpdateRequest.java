package com.learnai.dto.learning;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新学习步骤状态请求
 */
public record StepUpdateRequest(
        @NotBlank(message = "步骤状态不能为空")
        String status,
        Integer durationSeconds
) {
}
