package com.learnai.dto.market;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 提交 3D 模型请求（multipart 表单，文件单独用 @RequestParam 接收）
 */
@Data
public class ModelCreateRequest {

    @NotBlank(message = "模型名称不能为空")
    private String name;

    private String creator;

    @NotNull(message = "请选择分类")
    private Long categoryId;

    private BigDecimal price = BigDecimal.ZERO;

    private String previewUrl;

    private Boolean isPublic = true;
}
