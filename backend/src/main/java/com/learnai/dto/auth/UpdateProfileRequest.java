package com.learnai.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(max = 30, message = "学号长度不能超过 30 个字符")
        String studentNo,
        String gender,
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String phone,
        String email,
        String location,
        LocalDate birthdate,
        String bio,
        String province,
        String city,
        String defaultShippingAddress
) {
}
