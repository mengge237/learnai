package com.learnai.dto.auth;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UpdateProfileRequest(
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
