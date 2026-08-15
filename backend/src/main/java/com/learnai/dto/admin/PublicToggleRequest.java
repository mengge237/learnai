package com.learnai.dto.admin;

import jakarta.validation.constraints.NotNull;

/**
 * 上架 / 下架请求
 */
public record PublicToggleRequest(@NotNull Boolean isPublic) {
}
