package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 收藏（model_id / resource_id 二选一；MySQL 唯一索引允许多个 NULL，天然去重）
 */
@Entity
@Table(name = "favorite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "resource_id"}),
                @UniqueConstraint(columnNames = {"user_id", "model_id"})
        })
@Getter
@Setter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate = LocalDateTime.now();
}
