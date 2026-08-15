package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 答疑交互记录
 */
@Entity
@Table(name = "ai_interaction",
        indexes = @Index(columnList = "user_id, interaction_time"))
@Getter
@Setter
@NoArgsConstructor
public class AiInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Long interactionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "ai_message", columnDefinition = "TEXT")
    private String aiMessage;

    @Column(name = "interaction_time", nullable = false)
    private LocalDateTime interactionTime = LocalDateTime.now();

    @Column(name = "interaction_type", length = 50)
    private String interactionType;

    @Column(name = "topic", length = 50)
    private String topic;
}
