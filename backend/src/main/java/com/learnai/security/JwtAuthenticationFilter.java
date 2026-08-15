package com.learnai.security;

import com.learnai.entity.User;
import com.learnai.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 从 Authorization: Bearer <token> 解析 JWT；
 * 每次请求都从数据库重新加载用户，保证角色变更即时生效
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jwt jwt = jwtService.decode(token);
                Long uid = jwt.getClaim("uid");
                if (uid != null) {
                    userRepository.findById(uid).ifPresent(user -> {
                        if (Boolean.TRUE.equals(user.getIsActive())) {
                            authenticate(user);
                        }
                    });
                }
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("JWT 解析失败: {}", e.getMessage());
                // token 无效 → 保持未认证状态，由 EntryPoint 返回 401
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(User user) {
        var authorities = List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        var auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
        auth.setDetails(user.getUserId());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
