package com.learnai.controller;

import com.learnai.dto.auth.*;
import com.learnai.security.SecurityUtils;
import com.learnai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto me() {
        return userService.getProfile(SecurityUtils.currentUserId());
    }

    @PutMapping("/me")
    public UserProfileDto updateMe(@Valid @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(SecurityUtils.currentUserId(), req);
    }

    @PutMapping("/me/password")
    public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(SecurityUtils.currentUserId(), req);
        return Map.of("message", "密码修改成功");
    }

    @GetMapping("/me/preferences")
    public PreferencesDto getPreferences() {
        return userService.getPreferences(SecurityUtils.currentUserId());
    }

    @PutMapping("/me/preferences")
    public PreferencesDto updatePreferences(@RequestBody PreferencesDto req) {
        return userService.updatePreferences(SecurityUtils.currentUserId(), req);
    }
}
