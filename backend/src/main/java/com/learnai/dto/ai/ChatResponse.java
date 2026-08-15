package com.learnai.dto.ai;

import java.time.LocalDateTime;

/**
 * AI 聊天响应（provider：llm = 真实大模型，rule = 规则式演示/回退）
 */
public record ChatResponse(
        String userMessage,
        String aiMessage,
        LocalDateTime interactionTime,
        String provider
) {
}
