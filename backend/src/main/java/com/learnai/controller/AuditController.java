package com.learnai.controller;

import com.learnai.dto.audit.AuditRecordDto;
import com.learnai.dto.audit.AuditRequest;
import com.learnai.dto.audit.AuditStatsDto;
import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.dto.market.ModelDto;
import com.learnai.security.SecurityUtils;
import com.learnai.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审核：待审核列表与通过/驳回（审核员与管理员）
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
public class AuditController {

    private final AuditService auditService;

    /** 审核工作台统计 */
    @GetMapping("/stats")
    public AuditStatsDto stats() {
        return auditService.stats();
    }

    /** 资源审核历史 */
    @GetMapping("/history/resources")
    public PageResponse<AuditRecordDto> historyResources(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return auditService.historyResources(page, size);
    }

    /** 模型审核历史 */
    @GetMapping("/history/models")
    public PageResponse<AuditRecordDto> historyModels(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return auditService.historyModels(page, size);
    }

    @GetMapping("/resources")
    public List<ResourceDto> pendingResources() {
        return auditService.pendingResources();
    }

    @PostMapping("/resources/{id}/review")
    public ResourceDto reviewResource(@PathVariable Long id, @Valid @RequestBody AuditRequest req) {
        return auditService.reviewResource(id, req, SecurityUtils.currentUserId());
    }

    @GetMapping("/models")
    public List<ModelDto> pendingModels() {
        return auditService.pendingModels();
    }

    @PostMapping("/models/{id}/review")
    public ModelDto reviewModel(@PathVariable Long id, @Valid @RequestBody AuditRequest req) {
        return auditService.reviewModel(id, req, SecurityUtils.currentUserId());
    }
}
