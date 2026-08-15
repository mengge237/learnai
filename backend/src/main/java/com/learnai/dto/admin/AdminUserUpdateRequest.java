package com.learnai.dto.admin;

/**
 * 管理员更新用户请求（改角色 / 启用禁用）
 */
public record AdminUserUpdateRequest(
        Long roleId,
        Boolean isActive
) {
}
