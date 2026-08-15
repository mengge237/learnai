package com.learnai.controller;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.MyPathDto;
import com.learnai.dto.learning.PathCreateRequest;
import com.learnai.dto.learning.PathDetailDto;
import com.learnai.dto.learning.PathDto;
import com.learnai.dto.learning.PathResourcesRequest;
import com.learnai.security.SecurityUtils;
import com.learnai.service.LearningPathService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学习路径：公开浏览、报名、我的路径、管理员维护
 */
@RestController
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService pathService;

    @GetMapping
    public PageResponse<PathDto> list(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "6") int size) {
        return pathService.list(page, size);
    }

    @GetMapping("/{id}")
    public PathDetailDto detail(@PathVariable Long id) {
        return pathService.detail(id);
    }

    @GetMapping("/my")
    public List<MyPathDto> my() {
        return pathService.myPaths(SecurityUtils.currentUserId());
    }

    @PostMapping("/{id}/enroll")
    public MyPathDto enroll(@PathVariable Long id) {
        return pathService.enroll(SecurityUtils.currentUserId(), id);
    }

    // ---------- 管理员维护 ----------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PathDto create(@Valid @RequestBody PathCreateRequest req) {
        return pathService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PathDto update(@PathVariable Long id, @Valid @RequestBody PathCreateRequest req) {
        return pathService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> delete(@PathVariable Long id) {
        pathService.delete(id);
        return Map.of("message", "删除成功");
    }

    @PutMapping("/{id}/resources")
    @PreAuthorize("hasRole('ADMIN')")
    public PathDetailDto updateResources(@PathVariable Long id,
                                         @Valid @RequestBody PathResourcesRequest req) {
        return pathService.updateResources(id, req.resourceIds());
    }
}
