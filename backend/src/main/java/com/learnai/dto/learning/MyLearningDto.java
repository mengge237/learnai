package com.learnai.dto.learning;

import java.time.LocalDateTime;

/**
 * 我的学习列表项（学习记录 + 资源概要）
 */
public record MyLearningDto(
        Long resourceId,
        String title,
        String categoryName,
        String thumbnailUrl,
        String status,
        Double progress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        Integer score,
        Boolean isFree,
        String difficultyLevel
) {
}
