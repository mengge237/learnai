package com.learnai.dto.interaction;

import java.time.LocalDateTime;

/**
 * 下载历史条目（type: resource / model）
 */
public record DownloadItemDto(
        Long downloadId,
        String type,
        Long targetId,
        String title,
        LocalDateTime downloadTime
) {
}
