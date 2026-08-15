package com.learnai.entity;

import com.learnai.entity.enums.StepStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 学习步骤（属于某条学习记录）
 */
@Entity
@Table(name = "learning_step")
@Getter
@Setter
@NoArgsConstructor
public class LearningStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "step_title", length = 200)
    private String stepTitle;

    /** 步骤正文（教程内容，可在线阅读） */
    @Column(name = "step_content", columnDefinition = "TEXT")
    private String stepContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StepStatus status = StepStatus.NotStarted;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}
