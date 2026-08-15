package com.learnai.controller;

import com.learnai.dto.audit.AuditRequest;
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
