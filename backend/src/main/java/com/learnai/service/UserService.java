package com.learnai.service;

import com.learnai.dto.auth.*;
import com.learnai.entity.User;
import com.learnai.exception.ApiException;
import com.learnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        return UserProfileDto.from(findUser(userId));
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findUser(userId);
        user.setGender(req.gender());
        user.setPhone(req.phone());
        user.setEmail(req.email());
        user.setLocation(req.location());
        user.setBirthdate(req.birthdate());
        user.setBio(req.bio());
        user.setProvince(req.province());
        user.setCity(req.city());
        user.setDefaultShippingAddress(req.defaultShippingAddress());
        return UserProfileDto.from(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw ApiException.badRequest("原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
    }

    @Transactional(readOnly = true)
    public PreferencesDto getPreferences(Long userId) {
        return PreferencesDto.from(findUser(userId));
    }

    @Transactional
    public PreferencesDto updatePreferences(Long userId, PreferencesDto req) {
        User user = findUser(userId);
        if (req.fontSize() != null) user.setFontSize(req.fontSize());
        if (req.borderColor() != null) user.setBorderColor(req.borderColor());
        if (req.themeColor() != null) user.setThemeColor(req.themeColor());
        if (req.darkMode() != null) user.setDarkMode(req.darkMode());
        if (req.sidebarPosition() != null) user.setSidebarPosition(req.sidebarPosition());
        if (req.animationSpeed() != null) user.setAnimationSpeed(req.animationSpeed());
        return PreferencesDto.from(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("用户不存在"));
    }
}
