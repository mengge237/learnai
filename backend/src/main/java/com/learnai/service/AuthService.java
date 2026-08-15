package com.learnai.service;

import com.learnai.dto.auth.LoginRequest;
import com.learnai.dto.auth.LoginResponse;
import com.learnai.dto.auth.RegisterRequest;
import com.learnai.dto.auth.UserProfileDto;
import com.learnai.entity.User;
import com.learnai.entity.UserRole;
import com.learnai.exception.ApiException;
import com.learnai.repository.UserRepository;
import com.learnai.repository.UserRoleRepository;
import com.learnai.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    /** 普通用户角色固定为 role_id = 3（种子数据保证） */
    private static final long NORMAL_USER_ROLE_ID = 3L;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * 注册：不接受 roleId，强制普通用户角色（修复旧系统注册选角色漏洞）
     */
    @Transactional
    public UserProfileDto register(RegisterRequest req) {
        if (!req.password().equals(req.confirmPassword())) {
            throw ApiException.badRequest("两次输入的密码不一致");
        }
        if (userRepository.existsByUsername(req.username())) {
            throw ApiException.conflict("用户名已存在");
        }

        UserRole normalRole = userRoleRepository.findById(NORMAL_USER_ROLE_ID)
                .orElseThrow(() -> new IllegalStateException("普通用户角色缺失，请先初始化角色数据"));

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(normalRole);
        user.setGender(req.gender());
        user.setPhone(req.phone());
        user.setEmail(req.email());
        user.setLocation(req.location());
        user.setBirthdate(req.birthdate());
        user.setIsActive(true);
        userRepository.save(user);

        return UserProfileDto.from(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> ApiException.unauthorized("用户名或密码错误"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw ApiException.unauthorized("用户名或密码错误");
        }
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw ApiException.forbidden("账号已被禁用，请联系管理员");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, UserProfileDto.from(user));
    }

    @Transactional(readOnly = true)
    public UserProfileDto me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("用户不存在"));
        return UserProfileDto.from(user);
    }
}
