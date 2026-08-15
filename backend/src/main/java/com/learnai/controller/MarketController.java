package com.learnai.controller;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.market.ModelCreateRequest;
import com.learnai.dto.market.ModelDto;
import com.learnai.security.SecurityUtils;
import com.learnai.service.MarketService;
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
import java.util.Map;

/**
 * 3D 模型商城：目录浏览（公开）、提交（待审核）、下载（登录）
 */
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping
    public PageResponse<ModelDto> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        return marketService.listModels(categoryId, search, page, size, sort);
    }

    @GetMapping("/{id}")
    public ModelDto detail(@PathVariable Long id) {
        return marketService.modelDetail(id);
    }

    @PostMapping
    public ModelDto create(@Valid @ModelAttribute ModelCreateRequest req,
                           @RequestParam("file") MultipartFile file) {
        return marketService.createModel(req, file);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) {
        MarketService.StoredFile stored =
                marketService.downloadModel(SecurityUtils.currentUserId(), id, request.getRemoteAddr());
        String encoded = URLEncoder.encode(stored.originalName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stored.resource());
    }

    // ---------- 模型分类 ----------

    @RestController
    @RequestMapping("/api/model-categories")
    @RequiredArgsConstructor
    public static class ModelCategoryController {

        private final MarketService marketService;

        @GetMapping
        public List<Map<String, Object>> categories() {
            return marketService.modelCategories();
        }
    }
}
