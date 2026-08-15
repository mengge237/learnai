package com.learnai.dto.learning;

import com.learnai.entity.LearningPath;

import java.time.LocalDateTime;

/**
 * 学习路径概要
 */
public record PathDto(
        Long id,
        String name,
        String description,
        String targetAudience,
        Integer estimatedHours,
        Integer difficultyLevel,
        Integer viewCount,
        Integer enrollmentCount,
        LocalDateTime createDate,
        Boolean isActive,
        String coverImageUrl,
        Integer resourceCount
) {
    public static PathDto from(LearningPath p, int resourceCount) {
        return new PathDto(
                p.getPathId(),
                p.getPathName(),
                p.getDescription(),
                p.getTargetAudience(),
                p.getEstimatedHours(),
                p.getDifficultyLevel(),
                p.getViewCount(),
                p.getEnrollmentCount(),
                p.getCreateDate(),
                p.getIsActive(),
                p.getCoverImageUrl(),
                resourceCount);
    }
}
