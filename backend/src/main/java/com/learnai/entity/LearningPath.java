package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 学习路径（一系列学习资源的组合）
 */
@Entity
@Table(name = "learning_path")
@Getter
@Setter
@NoArgsConstructor
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Long pathId;

    @Column(name = "path_name", nullable = false, length = 100)
    private String pathName;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "target_audience", length = 500)
    private String targetAudience;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel = 1;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "enrollment_count", nullable = false)
    private Integer enrollmentCount = 0;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
}
