package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 学习路径与资源的关联（按 sequence_number 排序）
 */
@Entity
@Table(name = "path_resource",
        uniqueConstraints = @UniqueConstraint(columnNames = {"path_id", "resource_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PathResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_resource_id")
    private Long pathResourceId;

    @Column(name = "path_id", nullable = false)
    private Long pathId;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;
}
