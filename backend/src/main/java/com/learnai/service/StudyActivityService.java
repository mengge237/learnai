package com.learnai.service;

import com.learnai.dto.study.HeartbeatRequest;
import com.learnai.dto.study.StudyStatsDto;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.StudyLog;
import com.learnai.entity.enums.LearningStatus;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.StudyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 学习活动与激励：心跳上报学习时长（按日聚合到 study_log）、
 * 学习状态检测（内存心跳窗口，判断「是否正在学习」）、连续打卡统计。
 */
@Service
@RequiredArgsConstructor
public class StudyActivityService {

    private static final Map<DayOfWeek, String> DAY_NAMES = Map.of(
            DayOfWeek.MONDAY, "周一",
            DayOfWeek.TUESDAY, "周二",
            DayOfWeek.WEDNESDAY, "周三",
            DayOfWeek.THURSDAY, "周四",
            DayOfWeek.FRIDAY, "周五",
            DayOfWeek.SATURDAY, "周六",
            DayOfWeek.SUNDAY, "周日");

    /** 心跳窗口：多少秒内有上报视为「正在学习」 */
    private static final long STUDYING_WINDOW_SECONDS = 90;

    private final StudyLogRepository studyLogRepository;
    private final LearningRecordRepository recordRepository;
    private final LearningResourceRepository resourceRepository;

    /** 学习状态（内存）：userId → {resourceId, resourceTitle, lastBeatAt} */
    private record Activity(Long resourceId, String resourceTitle, LocalDateTime lastBeatAt) {
    }

    private final Map<Long, Activity> activities = new ConcurrentHashMap<>();

    /** 学习心跳：按秒上报，折算分钟累加到今日学习日志；同时更新学习记录的累计时长 */
    @Transactional
    public StudyStatsDto heartbeat(Long userId, HeartbeatRequest req) {
        int seconds = req.seconds() == null ? 0 : Math.max(0, Math.min(req.seconds(), 300));
        if (seconds > 0) {
            int addMinutes = Math.max(1, Math.round(seconds / 60.0f));
            LocalDate today = LocalDate.now();
            StudyLog log = studyLogRepository.findByUserIdAndStudyDate(userId, today).orElseGet(() -> {
                StudyLog l = new StudyLog();
                l.setUserId(userId);
                l.setStudyDate(today);
                return l;
            });
            log.setMinutes(log.getMinutes() + addMinutes);
            log.setUpdatedAt(LocalDateTime.now());
            studyLogRepository.save(log);

            if (req.resourceId() != null) {
                recordRepository.findByUserIdAndResourceId(userId, req.resourceId()).ifPresent(rec -> {
                    if (rec.getStatus() != LearningStatus.Completed) {
                        rec.setDurationMinutes((rec.getDurationMinutes() == null ? 0 : rec.getDurationMinutes())
                                + addMinutes);
                    }
                });
            }
        }
        String title = null;
        if (req.resourceId() != null) {
            title = resourceRepository.findById(req.resourceId())
                    .map(LearningResource::getResourceTitle).orElse(null);
        }
        activities.put(userId, new Activity(req.resourceId(), title, LocalDateTime.now()));
        return stats(userId);
    }

    /** 学习激励统计总览 */
    @Transactional(readOnly = true)
    public StudyStatsDto stats(Long userId) {
        LocalDate today = LocalDate.now();
        List<StudyLog> logs = studyLogRepository.findByUserIdAndStudyDateGreaterThanEqualOrderByStudyDateAsc(
                userId, today.minusDays(6));
        Map<LocalDate, Integer> minutesByDate = new HashMap<>();
        logs.forEach(l -> minutesByDate.merge(l.getStudyDate(), l.getMinutes(), Integer::sum));

        int todayMinutes = minutesByDate.getOrDefault(today, 0);
        List<StudyStatsDto.DailyStudyDto> week = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            week.add(new StudyStatsDto.DailyStudyDto(date, DAY_NAMES.get(date.getDayOfWeek()),
                    minutesByDate.getOrDefault(date, 0)));
        }

        int totalMinutes = logs.stream().mapToInt(StudyLog::getMinutes).sum();
        if (totalMinutes == 0) {
            // 历史数据兜底：没有心跳日志时用学习记录的累计时长
            totalMinutes = recordRepository.findByUserId(userId).stream()
                    .mapToInt(r -> r.getDurationMinutes() == null ? 0 : r.getDurationMinutes()).sum();
        }

        int completedCount = (int) recordRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() == LearningStatus.Completed).count();

        Activity act = activities.get(userId);
        boolean isStudying = act != null
                && ChronoUnit.SECONDS.between(act.lastBeatAt(), LocalDateTime.now()) <= STUDYING_WINDOW_SECONDS;

        return new StudyStatsDto(
                todayMinutes, totalMinutes, streakDays(minutesByDate, today), week,
                completedCount, isStudying,
                isStudying ? act.resourceTitle() : null,
                act == null ? null : act.lastBeatAt());
    }

    /** 连续打卡天数：从今天（今天未学则从昨天）往回数连续有学习记录的天数 */
    private int streakDays(Map<LocalDate, Integer> minutesByDate, LocalDate today) {
        LocalDate cursor = minutesByDate.getOrDefault(today, 0) > 0 ? today : today.minusDays(1);
        int streak = 0;
        while (minutesByDate.getOrDefault(cursor, 0) > 0) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }
}
