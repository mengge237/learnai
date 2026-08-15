package com.learnai.dto.ai;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习分析总览
 */
public record AnalyticsDto(
        int totalLearningResources,
        int totalCompleted,
        int totalInProgress,
        long totalAIInteractions,
        double averageProgress,
        int totalLearningMinutes,
        List<CategoryStatDto> categoryStats,
        List<WeeklyStatDto> weeklyStats,
        List<RecentRecordDto> recentRecords
) {
    /** 分类统计 */
    public record CategoryStatDto(
            String categoryName,
            int totalResources,
            int completedResources,
            double avgProgress
    ) {
    }

    /** 近 7 天学习统计 */
    public record WeeklyStatDto(
            LocalDate date,
            String dayName,
            int totalLearning,
            int completed
    ) {
    }

    /** 最近学习记录 */
    public record RecentRecordDto(
            Long resourceId,
            String title,
            String status,
            Double progress,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
    }
}
