package com.learnai.controller;

import com.learnai.dto.study.HeartbeatRequest;
import com.learnai.dto.study.StudyStatsDto;
import com.learnai.security.SecurityUtils;
import com.learnai.service.StudyActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学习活动与激励：心跳上报、学习统计（均需登录）。
 */
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyActivityService studyService;

    /** 学习心跳：学习页每 30 秒上报一次实际学习时长 */
    @PostMapping("/heartbeat")
    public StudyStatsDto heartbeat(@Valid @RequestBody HeartbeatRequest req) {
        return studyService.heartbeat(SecurityUtils.currentUserId(), req);
    }

    /** 学习激励统计：今日/累计/连续打卡/周统计/学习状态 */
    @GetMapping("/stats")
    public StudyStatsDto stats() {
        return studyService.stats(SecurityUtils.currentUserId());
    }
}
