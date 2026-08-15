package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日学习日志（每人每天一条）：由学习页心跳上报累加学习分钟数，
 * 用于「今日时长 / 连续打卡 / 周统计」等学习激励功能。
 */
@Entity
@Table(name = "study_log",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "study_date"}))
@Getter
@Setter
@NoArgsConstructor
public class StudyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(name = "minutes", nullable = false)
    private Integer minutes = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
