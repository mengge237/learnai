package com.learnai.dto.learning;

import com.learnai.entity.LearningStep;

import java.time.LocalDateTime;

/**
 * 学习步骤
 */
public record StepDto(
        Integer stepNumber,
        String stepTitle,
        String status,
        LocalDateTime completedTime,
        Integer durationSeconds
) {
    public static StepDto from(LearningStep s) {
        return new StepDto(
                s.getStepNumber(),
                s.getStepTitle(),
                s.getStatus() == null ? null : s.getStatus().name(),
                s.getCompletedTime(),
                s.getDurationSeconds());
    }
}
