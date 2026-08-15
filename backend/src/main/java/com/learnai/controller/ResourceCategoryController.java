package com.learnai.controller;

import com.learnai.dto.learning.CategoryNodeDto;
import com.learnai.service.ResourceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ResourceCategoryController {

    private final ResourceCategoryService categoryService;

    @GetMapping
    public List<CategoryNodeDto> tree() {
        return categoryService.tree();
    }
}
