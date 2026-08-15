package com.learnai.controller;

import com.learnai.dto.learning.CategoryAdminDto;
import com.learnai.dto.learning.CategorySaveRequest;
import com.learnai.service.ResourceCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员：学习资源分类管理（增删改查）
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final ResourceCategoryService categoryService;

    /** 全部分类（含停用），带资源数 */
    @GetMapping
    public List<CategoryAdminDto> list() {
        return categoryService.adminList();
    }

    @PostMapping
    public CategoryAdminDto create(@Valid @RequestBody CategorySaveRequest req) {
        return categoryService.create(req);
    }

    @PutMapping("/{id}")
    public CategoryAdminDto update(@PathVariable Long id, @Valid @RequestBody CategorySaveRequest req) {
        return categoryService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
