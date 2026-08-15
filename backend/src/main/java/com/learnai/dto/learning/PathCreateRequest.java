package com.learnai.dto.learning;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建/更新学习路径请求（管理员）
 */
@Data
public class PathCreateRequest {

    @NotBlank(message = "路径名称不能为空")
    private String name;

    private String description;

    private String targetAudience;

    private Integer estimatedHours;

    private Integer difficultyLevel = 1;

    private String coverImageUrl;

    private Boolean isActive = true;
}
