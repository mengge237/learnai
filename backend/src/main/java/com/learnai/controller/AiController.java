package com.learnai.controller;

import com.learnai.dto.ai.AiHistoryItemDto;
import com.learnai.dto.ai.AnalyticsDto;
import com.learnai.dto.ai.ChatRequest;
import com.learnai.dto.ai.ChatResponse;
import com.learnai.dto.ai.RecommendResponse;
import com.learnai.security.SecurityUtils;
import com.learnai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习答疑（均需登录）
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/history")
    public List<AiHistoryItemDto> history() {
        return aiService.history(SecurityUtils.currentUserId());
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest req) {
        return aiService.chat(SecurityUtils.currentUserId(), req);
    }

    @GetMapping("/recommend")
    public RecommendResponse recommend() {
        return aiService.recommend(SecurityUtils.currentUserId());
    }

    @GetMapping("/analytics")
    public AnalyticsDto analytics() {
        return aiService.analytics(SecurityUtils.currentUserId());
    }
}
