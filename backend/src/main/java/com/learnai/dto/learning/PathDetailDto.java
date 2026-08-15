package com.learnai.dto.learning;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习路径详情（含按序资源列表）
 */
public record PathDetailDto(
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
        List<ResourceDto> resources
) {
}
