package com.learnai.entity;

import com.learnai.entity.enums.LearningStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户学习某个资源的记录（每人每资源一条）
 */
@Entity
@Table(name = "learning_record",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource_id"}))
@Getter
@Setter
@NoArgsConstructor
public class LearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LearningStatus status = LearningStatus.NotStarted;

    @Column(name = "progress", nullable = false)
    private Double progress = 0.0;

    @Column(name = "score")
    private Integer score;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}
