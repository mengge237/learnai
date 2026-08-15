package com.learnai.dto.learning;

import com.learnai.entity.LearningResource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习资源 DTO（列表与详情共用）
 */
public record ResourceDto(
        Long id,
        String title,
        String author,
        String resourceCode,
        BigDecimal price,
        String previewUrl,
        String thumbnailUrl,
        Long categoryId,
        String categoryName,
        String description,
        String difficultyLevel,
        Integer durationMinutes,
        String learningType,
        Integer viewCount,
        Integer likeCount,
        Integer completionCount,
        LocalDateTime createDate,
        Boolean isFree,
        Boolean isApproved,
        Boolean isPublic,
        String rejectionReason,
        String videoUrl,
        String originalFileName,
        String filePath
) {
    public static ResourceDto from(LearningResource r) {
        return new ResourceDto(
                r.getResourceId(),
                r.getResourceTitle(),
                r.getAuthor(),
                r.getResourceCode(),
                r.getPrice(),
                r.getPreviewUrl(),
                r.getThumbnailUrl(),
                r.getCategory() == null ? null : r.getCategory().getCategoryId(),
                r.getCategory() == null ? null : r.getCategory().getCategoryName(),
                r.getDescription(),
                r.getDifficultyLevel(),
                r.getDurationMinutes(),
                r.getLearningType(),
                r.getViewCount(),
                r.getLikeCount(),
                r.getCompletionCount(),
                r.getCreateDate(),
                r.getIsFree(),
                r.getIsApproved(),
                r.getIsPublic(),
                r.getRejectionReason(),
                r.getVideoUrl(),
                r.getOriginalFileName(),
                r.getFilePath());
    }
}
