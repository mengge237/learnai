package com.learnai.controller;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.MyLearningDto;
import com.learnai.dto.learning.ResourceCreateRequest;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.security.SecurityUtils;
import com.learnai.service.LearningResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 学习资源：公开浏览（列表/详情）、提交（待审核）、点赞、下载、我的学习
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceService resourceService;

    @GetMapping
    public PageResponse<ResourceDto> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        return resourceService.list(categoryId, search, page, size, sort);
    }

    @GetMapping("/{id}")
    public ResourceDto detail(@PathVariable Long id) {
        return resourceService.detail(id);
    }

    /** 提交学习资源（multipart 表单，登录用户可提交，审核通过后公开） */
    @PostMapping
    public ResourceDto create(@Valid @ModelAttribute ResourceCreateRequest req,
                              @RequestParam("file") MultipartFile file) {
        return resourceService.create(req, file);
    }

    @PostMapping("/{id}/like")
    public ResourceDto like(@PathVariable Long id) {
        return resourceService.like(id);
    }

    /** 下载资源文件（需登录，记录下载历史；RFC 5987 支持中文文件名） */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) {
        LearningResourceService.StoredFile stored =
                resourceService.getDownloadFile(SecurityUtils.currentUserId(), id, request.getRemoteAddr());
        String encoded = URLEncoder.encode(stored.originalName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stored.resource());
    }

    @GetMapping("/my-learning")
    public List<MyLearningDto> myLearning() {
        return resourceService.myLearning(SecurityUtils.currentUserId());
    }
}
