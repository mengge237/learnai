package com.learnai.service.ai;

/**
 * 答疑上下文：用户问题 + 当前学习资源 + 学习进度概况（供大模型与规则引擎共用）
 */
public record ChatContext(
        Long userId,
        String message,
        Long resourceId,
        String resourceTitle,     // 关联资源标题（可为 null）
        String progressSummary    // 学习进度文字概况（可为 null）
) {
}
