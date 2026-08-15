package com.learnai.controller;

import com.learnai.dto.admin.AdminOrderDto;
import com.learnai.dto.admin.AdminStatsDto;
import com.learnai.dto.admin.AdminUserDto;
import com.learnai.dto.admin.AdminUserUpdateRequest;
import com.learnai.dto.admin.PublicToggleRequest;
import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.dto.market.ModelDto;
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

    // ---------- 资源管理 ----------

    /** 全部资源（status: all/pending/approved/rejected + 标题搜索） */
    @GetMapping("/resources")
    public PageResponse<ResourceDto> resources(@RequestParam(required = false) String search,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return adminService.listResources(search, status, page, size);
    }

    /** 上架 / 下架 */
    @PutMapping("/resources/{id}/public")
    public ResourceDto toggleResourcePublic(@PathVariable Long id, @Valid @RequestBody PublicToggleRequest req) {
        return adminService.toggleResourcePublic(id, req.isPublic());
    }

    @DeleteMapping("/resources/{id}")
    public void deleteResource(@PathVariable Long id) {
        adminService.deleteResource(id);
    }

    // ---------- 模型管理 ----------

    /** 全部模型（status: all/pending/approved/rejected + 名称搜索） */
    @GetMapping("/models")
    public PageResponse<ModelDto> models(@RequestParam(required = false) String search,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return adminService.listModels(search, status, page, size);
    }

    /** 上架 / 下架 */
    @PutMapping("/models/{id}/public")
    public ModelDto toggleModelPublic(@PathVariable Long id, @Valid @RequestBody PublicToggleRequest req) {
        return adminService.toggleModelPublic(id, req.isPublic());
    }

    @DeleteMapping("/models/{id}")
    public void deleteModel(@PathVariable Long id) {
        adminService.deleteModel(id);
    }

    // ---------- 订单管理 ----------

    /** 全部订单（可按状态筛选） */
    @GetMapping("/orders")
    public PageResponse<AdminOrderDto> orders(@RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return adminService.listOrders(status, page, size);
    }
}
