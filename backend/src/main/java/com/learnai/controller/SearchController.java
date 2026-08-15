package com.learnai.controller;

import com.learnai.dto.search.GlobalSearchResponse;
import com.learnai.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局搜索：课程 / 路径 / 模型 三类内容统一检索（游客可用）
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public GlobalSearchResponse search(@RequestParam String keyword) {
        return searchService.search(keyword);
    }
}
