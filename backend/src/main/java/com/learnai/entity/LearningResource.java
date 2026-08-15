package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习资源（教程内容，核心实体）
 */
@Entity
@Table(name = "learning_resource")
@Getter
@Setter
@NoArgsConstructor
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "resource_title", nullable = false, length = 100)
    private String resourceTitle;

    @Column(name = "author", length = 50)
    private String author;

    @Column(name = "resource_code", length = 50)
    private String resourceCode;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "preview_url", length = 500)
    private String previewUrl;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private ResourceCategory category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    // ---------- 审核字段 ----------

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // ---------- 内容字段 ----------

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "learning_type", length = 50)
    private String learningType;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "completion_count", nullable = false)
    private Integer completionCount = 0;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "prerequisite_resource_id")
    private Long prerequisiteResourceId;

    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;
}
