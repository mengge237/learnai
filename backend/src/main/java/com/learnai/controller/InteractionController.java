package com.learnai.controller;

import com.learnai.dto.interaction.CommentCreateRequest;
import com.learnai.dto.interaction.CommentDto;
import com.learnai.dto.interaction.DownloadItemDto;
import com.learnai.dto.interaction.FavoriteItemDto;
import com.learnai.dto.interaction.FavoriteToggleRequest;
import com.learnai.security.SecurityUtils;
import com.learnai.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 互动：收藏、评论、下载历史
 */
@RestController
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    // ---------- 收藏 ----------

    @GetMapping("/api/favorites")
    public List<FavoriteItemDto> myFavorites() {
        return interactionService.myFavorites(SecurityUtils.currentUserId());
    }

    /** 收藏/取消收藏切换，返回 { favorited: true/false } */
    @PostMapping("/api/favorites/toggle")
    public Map<String, Boolean> toggle(@RequestBody FavoriteToggleRequest req) {
        boolean favorited = interactionService.toggleFavorite(
                SecurityUtils.currentUserId(), req.resourceId(), req.modelId());
        return Map.of("favorited", favorited);
    }

    // ---------- 评论 ----------

    @GetMapping("/api/comments")
    public List<CommentDto> comments(@RequestParam(required = false) Long resourceId,
                                     @RequestParam(required = false) Long modelId) {
        return interactionService.listComments(resourceId, modelId);
    }

    @PostMapping("/api/comments")
    public CommentDto add(@Valid @RequestBody CommentCreateRequest req) {
        return interactionService.addComment(SecurityUtils.currentUserId(), req);
    }

    // ---------- 下载历史 ----------

    @GetMapping("/api/downloads")
    public List<DownloadItemDto> myDownloads() {
        return interactionService.myDownloads(SecurityUtils.currentUserId());
    }
}
