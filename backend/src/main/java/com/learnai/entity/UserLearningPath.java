package com.learnai.entity;

import com.learnai.entity.enums.PathStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户报名学习路径的记录
 */
@Entity
@Table(name = "user_learning_path",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "path_id"}))
@Getter
@Setter
@NoArgsConstructor
public class UserLearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_path_id")
    private Long userPathId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "path_id", nullable = false)
    private Long pathId;

    @Column(name = "enroll_date", nullable = false)
    private LocalDateTime enrollDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PathStatus status = PathStatus.InProgress;

    @Column(name = "progress", nullable = false)
    private Double progress = 0.0;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;
}
