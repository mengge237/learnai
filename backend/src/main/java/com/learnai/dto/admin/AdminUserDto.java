package com.learnai.dto.admin;

import com.learnai.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理端用户列表项
 */
public record AdminUserDto(
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
        Boolean isActive,
        LocalDateTime createdAt
) {
    public static AdminUserDto from(User u) {
        return new AdminUserDto(
                u.getUserId(),
                u.getUsername(),
                u.getStudentNo(),
                u.getRole() == null ? null : u.getRole().getRoleId(),
                u.getRole() == null ? null : u.getRole().getRoleName(),
                u.getGender(),
                u.getPhone(),
                u.getEmail(),
                u.getLocation(),
                u.getBirthdate(),
                u.getIsActive(),
                u.getCreatedAt());
    }
}
