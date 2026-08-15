package com.learnai.dto.interaction;

/**
 * 收藏/取消收藏请求（resourceId 与 modelId 二选一）
 */
public record FavoriteToggleRequest(
        Long resourceId,
        Long modelId
) {
}
