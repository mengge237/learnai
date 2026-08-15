package com.learnai.dto.interaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论（树形，replies 为子评论）
 */
public record CommentDto(
        Long id,
        Long parentId,
        Long userId,
        String username,
        String content,
        LocalDateTime commentDate,
        List<CommentDto> replies
) {
}
