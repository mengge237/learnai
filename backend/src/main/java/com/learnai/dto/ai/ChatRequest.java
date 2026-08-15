package com.learnai.dto.ai;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 聊天请求（resourceId 可选，用于资源上下文对话）
 */
public record ChatRequest(
        @NotBlank(message = "消息不能为空")
        String message,
        Long resourceId
) {
}
