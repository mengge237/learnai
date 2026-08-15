package com.learnai.dto.interaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏条目（资源或模型，type: resource / model）
 */
public record FavoriteItemDto(
        Long favoriteId,
        String type,
        Long targetId,
        String title,
        String cover,
        BigDecimal price,
        LocalDateTime addedDate
) {
}
