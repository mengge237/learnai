package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 评论（model_id / resource_id 二选一，普通 Long 列避免 JPA 双外键映射问题）
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    /** 父评论 ID，NULL 表示根评论 */
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "comment_date", nullable = false)
    private LocalDateTime commentDate = LocalDateTime.now();

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = true;
}
