package com.learnai.dto.study;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习激励统计：今日时长 / 累计时长 / 连续打卡 / 最近 7 天 / 学习状态。
 */
public record StudyStatsDto(
        Integer todayMinutes,
        Integer totalMinutes,
        Integer streakDays,
        List<DailyStudyDto> week,
        Integer completedCount,
        Boolean isStudying,
        String currentResourceTitle,
        LocalDateTime lastActiveAt
) {
    /** 单日学习时长 */
    public record DailyStudyDto(LocalDate date, String label, Integer minutes) {
    }
}
