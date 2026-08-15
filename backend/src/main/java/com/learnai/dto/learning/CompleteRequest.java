package com.learnai.dto.learning;

/**
 * 完成学习请求（分数、笔记可选）
 */
public record CompleteRequest(
        Integer score,
        String notes
) {
}
