package com.learnai.dto.ai;

import com.learnai.entity.AiInteraction;

import java.time.LocalDateTime;

/**
 * AI 对话历史条目
 */
public record AiHistoryItemDto(
        Long id,
        Long resourceId,
        String userMessage,
        String aiMessage,
        LocalDateTime interactionTime,
        String interactionType,
        String topic
) {
    public static AiHistoryItemDto from(AiInteraction i) {
        return new AiHistoryItemDto(
                i.getInteractionId(),
                i.getResourceId(),
                i.getUserMessage(),
                i.getAiMessage(),
                i.getInteractionTime(),
                i.getInteractionType(),
                i.getTopic());
    }
}
