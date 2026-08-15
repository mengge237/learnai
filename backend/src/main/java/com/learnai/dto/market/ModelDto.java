package com.learnai.dto.market;

import com.learnai.entity.Model3D;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 3D 模型商品 DTO
 */
public record ModelDto(
        Long id,
        String name,
        String creator,
        String modelCode,
        BigDecimal price,
        String previewUrl,
        Long categoryId,
        String categoryName,
        Boolean isApproved,
        Boolean isPublic,
        String rejectionReason,
        String originalFileName,
        String filePath,
        LocalDateTime createDate
) {
    public static ModelDto from(Model3D m) {
        return new ModelDto(
                m.getModelId(),
                m.getModelName(),
                m.getCreator(),
                m.getModelCode(),
                m.getPrice(),
                m.getPreviewUrl(),
                m.getCategory() == null ? null : m.getCategory().getCategoryId(),
                m.getCategory() == null ? null : m.getCategory().getCategoryName(),
                m.getIsApproved(),
                m.getIsPublic(),
                m.getRejectionReason(),
                m.getOriginalFileName(),
                m.getFilePath(),
                m.getCreateDate());
    }
}
