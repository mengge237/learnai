package com.learnai.security;

import com.learnai.entity.User;
import com.learnai.exception.ApiException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具
 */
public final class SecurityUtils {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_AUDITOR = "ROLE_AUDITOR";
    public static final String ROLE_USER = "ROLE_USER";

    private SecurityUtils() {
    }

    /** 获取当前登录用户（未登录抛 401） */
    public static User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user;
        }
        throw ApiException.unauthorized("未登录或登录已过期");
    }

    /** 获取当前登录用户 ID */
    public static Long currentUserId() {
        return currentUser().getUserId();
    }

    public static boolean isAdmin() {
        return hasRole(ROLE_ADMIN);
    }

    public static boolean isAuditor() {
        return hasRole(ROLE_AUDITOR);
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
