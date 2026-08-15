package com.learnai.dto.interaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 发表评论请求（resourceId 与 modelId 二选一；parentCommentId 可选表示回复）
 */
public record CommentCreateRequest(
        Long resourceId,
        Long modelId,
        Long parentCommentId,
        @NotBlank(message = "评论内容不能为空")
        @Size(max = 500, message = "评论最多 500 字")
        String content
) {
}
