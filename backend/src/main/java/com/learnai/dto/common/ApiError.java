package com.learnai.dto.common;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一错误响应体
 */
public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
