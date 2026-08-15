package com.learnai.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 注册请求 —— 不包含 roleId 字段，服务端强制注册为普通用户（修复旧系统选角色漏洞）
 */
public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 4, max = 20, message = "用户名长度需在 4-20 个字符之间")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 50, message = "密码长度需在 6-50 个字符之间")
        String password,

        @NotBlank(message = "请再次输入密码")
        String confirmPassword,

        /** 学号（校园特供版，选填） */
        @Size(max = 30, message = "学号长度不能超过 30 个字符")
        String studentNo,

        String gender,

        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String phone,

        String email,
        String location,
        LocalDate birthdate
) {
}
