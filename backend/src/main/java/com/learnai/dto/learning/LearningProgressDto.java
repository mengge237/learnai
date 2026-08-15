package com.learnai.dto.learning;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单个资源的学习进度（含步骤）
 */
public record LearningProgressDto(
        Long recordId,
        Long resourceId,
        String resourceTitle,
        String status,
        Double progress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer score,
        String notes,
        Integer durationMinutes,
        List<StepDto> steps
) {
}
