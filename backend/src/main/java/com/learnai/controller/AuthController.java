package com.learnai.controller;

import com.learnai.dto.auth.LoginRequest;
import com.learnai.dto.auth.LoginResponse;
import com.learnai.dto.auth.RegisterRequest;
import com.learnai.dto.auth.UserProfileDto;
import com.learnai.security.SecurityUtils;
import com.learnai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserProfileDto register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public UserProfileDto me() {
        return authService.me(SecurityUtils.currentUserId());
    }

    /** 无状态 JWT：登出由前端丢弃 token，这里仅为对称接口 */
    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "已退出登录");
    }
}
