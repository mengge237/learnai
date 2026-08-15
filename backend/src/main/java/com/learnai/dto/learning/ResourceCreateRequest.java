package com.learnai.dto.learning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 提交学习资源请求（multipart 表单，文件单独用 @RequestParam 接收）
 */
@Data
public class ResourceCreateRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "请选择分类")
    private Long categoryId;

    private BigDecimal price = BigDecimal.ZERO;

    private Boolean isFree = true;

    private String difficultyLevel;

    private Integer durationMinutes;

    private String learningType;

    private String videoUrl;

    private String previewUrl;

    private Boolean isPublic = true;
}
