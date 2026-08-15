package com.learnai.dto.auth;

import com.learnai.entity.User;

import java.time.LocalDate;

/**
 * 用户资料 + 个性化偏好（任何接口都不得返回 password）
 */
public record UserProfileDto(
        Long id,
        String username,
        String studentNo,
        Long roleId,
        String roleName,
        String gender,
        String phone,
        String email,
        String location,
        LocalDate birthdate,
        String bio,
        String province,
        String city,
        String defaultShippingAddress,
        Integer fontSize,
        String borderColor,
        String themeColor,
        Boolean darkMode,
        String themeMode,
        String sidebarPosition,
        String animationSpeed
) {
    public static UserProfileDto from(User u) {
        return new UserProfileDto(
                u.getUserId(),
                u.getUsername(),
                u.getStudentNo(),
                u.getRole().getRoleId(),
                u.getRole().getRoleName(),
                u.getGender(),
                u.getPhone(),
                u.getEmail(),
                u.getLocation(),
                u.getBirthdate(),
                u.getBio(),
                u.getProvince(),
                u.getCity(),
                u.getDefaultShippingAddress(),
                u.getFontSize(),
                u.getBorderColor(),
                u.getThemeColor(),
                u.getDarkMode(),
                u.getThemeMode(),
                u.getSidebarPosition(),
                u.getAnimationSpeed());
    }
}
