package com.learnai.controller;

import com.learnai.dto.learning.CompleteRequest;
import com.learnai.dto.learning.LearningProgressDto;
import com.learnai.dto.learning.ProgressUpdateRequest;
import com.learnai.dto.learning.StepUpdateRequest;
import com.learnai.security.SecurityUtils;
import com.learnai.service.LearningProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学习进度：开始/查询/更新进度/完成/步骤管理（均需登录）
 */
@RestController
@RequestMapping("/api/resources/{resourceId}/learn")
@RequiredArgsConstructor
public class LearningController {

    private final LearningProgressService progressService;

    @GetMapping
    public LearningProgressDto get(@PathVariable Long resourceId) {
        return progressService.get(SecurityUtils.currentUserId(), resourceId);
    }

    @PostMapping("/start")
    public LearningProgressDto start(@PathVariable Long resourceId) {
        return progressService.start(SecurityUtils.currentUserId(), resourceId);
    }

    @PutMapping("/progress")
    public LearningProgressDto updateProgress(@PathVariable Long resourceId,
                                              @Valid @RequestBody ProgressUpdateRequest req) {
        return progressService.updateProgress(SecurityUtils.currentUserId(), resourceId, req);
    }

    @PostMapping("/complete")
    public LearningProgressDto complete(@PathVariable Long resourceId,
                                        @RequestBody(required = false) CompleteRequest req) {
        return progressService.complete(SecurityUtils.currentUserId(), resourceId, req);
    }

    @PutMapping("/steps/{stepNumber}")
    public LearningProgressDto updateStep(@PathVariable Long resourceId,
                                          @PathVariable Integer stepNumber,
                                          @Valid @RequestBody StepUpdateRequest req) {
        return progressService.updateStep(SecurityUtils.currentUserId(), resourceId, stepNumber, req);
    }
}
