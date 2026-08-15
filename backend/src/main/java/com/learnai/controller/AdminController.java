package com.learnai.controller;

import com.learnai.dto.admin.AdminStatsDto;
import com.learnai.dto.admin.AdminUserDto;
import com.learnai.dto.admin.AdminUserUpdateRequest;
import com.learnai.dto.common.PageResponse;
import com.learnai.security.SecurityUtils;
import com.learnai.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员：用户管理、平台统计
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public PageResponse<AdminUserDto> users(@RequestParam(required = false) String search,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return adminService.listUsers(search, page, size);
    }

    @PutMapping("/users/{id}")
    public AdminUserDto updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest req) {
        return adminService.updateUser(SecurityUtils.currentUserId(), id, req);
    }

    @GetMapping("/stats")
    public AdminStatsDto stats() {
        return adminService.stats();
    }
}
