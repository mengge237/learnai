package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 下载记录
 */
@Entity
@Table(name = "download")
@Getter
@Setter
@NoArgsConstructor
public class Download {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "download_id")
    private Long downloadId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "download_time", nullable = false)
    private LocalDateTime downloadTime = LocalDateTime.now();

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}
