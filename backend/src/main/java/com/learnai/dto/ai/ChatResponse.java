package com.learnai.dto.ai;

import java.time.LocalDateTime;

/**
 * AI 聊天响应
 */
public record ChatResponse(
        String userMessage,
        String aiMessage,
        LocalDateTime interactionTime
) {
}
