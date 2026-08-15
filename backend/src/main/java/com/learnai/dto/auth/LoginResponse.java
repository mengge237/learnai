package com.learnai.dto.auth;

public record LoginResponse(
        String token,
        UserProfileDto user
) {
}
