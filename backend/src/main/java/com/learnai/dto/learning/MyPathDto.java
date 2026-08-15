package com.learnai.dto.learning;

import java.time.LocalDateTime;

/**
 * 我报名的学习路径
 */
public record MyPathDto(
        Long userPathId,
        Long pathId,
        String pathName,
        String coverImageUrl,
        String status,
        Double progress,
        LocalDateTime enrollDate,
        LocalDateTime completedDate,
        Integer estimatedHours
) {
}
