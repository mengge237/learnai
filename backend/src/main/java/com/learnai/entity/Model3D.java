package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 3D 模型商品（避免类名与常见 Model 冲突，命名 Model3D）
 */
@Entity
@Table(name = "model3d")
@Getter
@Setter
@NoArgsConstructor
public class Model3D {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    @Column(name = "creator", length = 50)
    private String creator;

    @Column(name = "model_code", length = 50)
    private String modelCode;

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
    private ModelCategory category;

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
}
