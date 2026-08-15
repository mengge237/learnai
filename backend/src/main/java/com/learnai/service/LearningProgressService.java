package com.learnai.service;

import com.learnai.dto.learning.CompleteRequest;
import com.learnai.dto.learning.LearningProgressDto;
import com.learnai.dto.learning.ProgressUpdateRequest;
import com.learnai.dto.learning.StepDto;
import com.learnai.dto.learning.StepUpdateRequest;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.LearningStep;
import com.learnai.entity.enums.LearningStatus;
import com.learnai.entity.enums.StepStatus;
import com.learnai.exception.ApiException;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.LearningStepRepository;
import com.learnai.service.factory.StepTemplateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习进度：开始/进度更新/完成/步骤管理
 */
@Service
@RequiredArgsConstructor
public class LearningProgressService {

    private final LearningRecordRepository recordRepository;
    private final LearningStepRepository stepRepository;
    private final LearningResourceRepository resourceRepository;

    /** 开始学习：没有记录则创建并生成默认步骤（幂等） */
    @Transactional
    public LearningProgressDto start(Long userId, Long resourceId) {
        LearningResource r = findResource(resourceId);
        LearningRecord record = recordRepository.findByUserIdAndResourceId(userId, resourceId)
                .orElseGet(() -> {
                    LearningRecord rec = new LearningRecord();
                    rec.setUserId(userId);
                    rec.setResourceId(resourceId);
                    rec.setStatus(LearningStatus.InProgress);
                    rec.setProgress(0.0);
                    rec = recordRepository.save(rec);
                    // 步骤模板统一由 StepTemplateFactory 提供（模板只定义一处）
                    int number = 1;
                    for (StepTemplateFactory.StepTemplate t : StepTemplateFactory.all()) {
                        LearningStep s = new LearningStep();
                        s.setRecordId(rec.getRecordId());
                        s.setStepNumber(number++);
                        s.setStepTitle(t.title());
                        s.setStepContent(t.render(r.getResourceTitle()));
                        s.setStatus(StepStatus.NotStarted);
                        stepRepository.save(s);
                    }
                    return rec;
                });
        if (record.getStatus() == LearningStatus.NotStarted) {
            record.setStatus(LearningStatus.InProgress);
        }
        return toDto(r, record);
    }

    /** 查询学习进度（未开始则返回空进度） */
    @Transactional(readOnly = true)
    public LearningProgressDto get(Long userId, Long resourceId) {
        LearningResource r = findResource(resourceId);
        return recordRepository.findByUserIdAndResourceId(userId, resourceId)
                .map(rec -> toDto(r, rec))
                .orElseGet(() -> new LearningProgressDto(
                        null, resourceId, r.getResourceTitle(),
                        LearningStatus.NotStarted.name(), 0.0,
                        null, null, null, null, null, List.of()));
    }

    @Transactional
    public LearningProgressDto updateProgress(Long userId, Long resourceId, ProgressUpdateRequest req) {
        LearningRecord record = mustFind(userId, resourceId);
        record.setProgress(req.progress());
        if (record.getStatus() == LearningStatus.NotStarted) {
            record.setStatus(LearningStatus.InProgress);
        }
        if (req.score() != null) {
            record.setScore(req.score());
        }
        if (req.notes() != null) {
            record.setNotes(req.notes());
        }
        if (req.durationMinutes() != null) {
            record.setDurationMinutes(req.durationMinutes());
        }
        return toDto(findResource(resourceId), record);
    }

    /** 完成学习：进度 100、资源完成数 +1 */
    @Transactional
    public LearningProgressDto complete(Long userId, Long resourceId, CompleteRequest req) {
        LearningRecord record = mustFind(userId, resourceId);
        boolean firstTime = record.getStatus() != LearningStatus.Completed;
        record.setStatus(LearningStatus.Completed);
        record.setProgress(100.0);
        record.setEndTime(LocalDateTime.now());
        if (req != null && req.score() != null) {
            record.setScore(req.score());
        }
        if (req != null && req.notes() != null) {
            record.setNotes(req.notes());
        }
        if (firstTime) {
            LearningResource r = findResource(resourceId);
            r.setCompletionCount(r.getCompletionCount() + 1);
        }
        for (LearningStep s : stepRepository.findByRecordIdOrderByStepNumberAsc(record.getRecordId())) {
            if (s.getStatus() != StepStatus.Skipped) {
                s.setStatus(StepStatus.Completed);
                s.setCompletedTime(LocalDateTime.now());
            }
        }
        return toDto(findResource(resourceId), record);
    }

    /** 更新步骤状态，并按完成比例联动总进度 */
    @Transactional
    public LearningProgressDto updateStep(Long userId, Long resourceId, Integer stepNumber, StepUpdateRequest req) {
        LearningRecord record = mustFind(userId, resourceId);
        StepStatus status;
        try {
            status = StepStatus.valueOf(req.status());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("无效的步骤状态: " + req.status());
        }
        LearningStep step = stepRepository.findByRecordIdAndStepNumber(record.getRecordId(), stepNumber)
                .orElseThrow(() -> ApiException.notFound("学习步骤不存在"));
        step.setStatus(status);
        if (status == StepStatus.Completed) {
            step.setCompletedTime(LocalDateTime.now());
        }
        if (req.durationSeconds() != null) {
            step.setDurationSeconds(req.durationSeconds());
        }
        List<LearningStep> steps = stepRepository.findByRecordIdOrderByStepNumberAsc(record.getRecordId());
        long done = steps.stream()
                .filter(s -> s.getStatus() == StepStatus.Completed || s.getStatus() == StepStatus.Skipped)
                .count();
        double progress = steps.isEmpty() ? record.getProgress()
                : Math.round(done * 1000.0 / steps.size()) / 10.0;
        record.setProgress(progress);
        if (progress >= 100 && record.getStatus() != LearningStatus.Completed) {
            record.setStatus(LearningStatus.Completed);
            record.setEndTime(LocalDateTime.now());
        }
        return toDto(findResource(resourceId), record);
    }

    private LearningRecord mustFind(Long userId, Long resourceId) {
        return recordRepository.findByUserIdAndResourceId(userId, resourceId)
                .orElseThrow(() -> ApiException.notFound("尚未开始学习该资源"));
    }

    private LearningResource findResource(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
    }

    private LearningProgressDto toDto(LearningResource r, LearningRecord rec) {
        List<StepDto> steps = stepRepository.findByRecordIdOrderByStepNumberAsc(rec.getRecordId())
                .stream().map(StepDto::from).toList();
        return new LearningProgressDto(
                rec.getRecordId(),
                rec.getResourceId(),
                r.getResourceTitle(),
                rec.getStatus() == null ? null : rec.getStatus().name(),
                rec.getProgress(),
                rec.getStartTime(),
                rec.getEndTime(),
                rec.getScore(),
                rec.getNotes(),
                rec.getDurationMinutes(),
                steps);
    }
}
