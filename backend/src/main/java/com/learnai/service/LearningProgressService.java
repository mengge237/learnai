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

    /** 开始学习时自动生成的默认步骤（标题 + 教程正文模板，%s 为资源名），种子数据同样引用 */
    public static final String[][] DEFAULT_STEPS = {
            {"了解内容概览",
                    "欢迎开始《%s》的学习！本步骤先带你建立整体认识。\n\n"
                            + "▍课程定位\n《%s》面向零基础入门到进阶的学习者，围绕核心概念与常用工具展开，"
                            + "配套演示模型与练习素材，边学边练。\n\n"
                            + "▍知识地图\n本课程共分为三个步骤：先了解概览 → 再深入学习与实践 → 最后完成练习与总结。"
                            + "建议按顺序完成，每一步都会解锁下一步的内容。\n\n"
                            + "▍学习建议\n阅读时随手做笔记；遇到不懂的概念，可以点击右下角的 AI 助手即时提问。"},
            {"深入学习与实践",
                    "本步骤是《%s》的核心内容，请跟随示例动手实践。\n\n"
                            + "▍核心要点\n1. 理解基础概念：先弄清「是什么」和「为什么」；\n"
                            + "2. 跟随示例操作：打开配套演示模型，一步步复现；\n"
                            + "3. 独立练习：脱离示例再操作一遍，检验掌握程度。\n\n"
                            + "▍常见问题\n- 操作卡住时，先查看上文步骤是否遗漏；\n"
                            + "- 报错信息是重要的线索，不要忽略；\n"
                            + "- 仍然解决不了？随时呼叫 AI 助手帮忙分析。\n\n"
                            + "▍实践任务\n完成一次完整的操作流程并截图保存，作为自己的学习成果。"},
            {"完成练习与总结",
                    "恭喜来到《%s》的最后一步！用练习检验成果，用总结沉淀知识。\n\n"
                            + "▍结课练习\n1. 独立完成本课程的实践任务；\n"
                            + "2. 用自己的话复述三个核心知识点；\n"
                            + "3. 对照学习目标检查是否全部达成。\n\n"
                            + "▍学习总结\n建议写下：本次学习的收获、遇到的难点、还想深入的方向。"
                            + "总结会保存在学习记录里，方便日后回顾。\n\n"
                            + "▍提交完成\n全部步骤完成后，点击「提交完成」为本次学习打分，"
                            + "系统会记录你的学习成就并计入学习分析。"}
    };

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
                    for (int i = 0; i < DEFAULT_STEPS.length; i++) {
                        LearningStep s = new LearningStep();
                        s.setRecordId(rec.getRecordId());
                        s.setStepNumber(i + 1);
                        s.setStepTitle(DEFAULT_STEPS[i][0]);
                        s.setStepContent(String.format(DEFAULT_STEPS[i][1], r.getResourceTitle()));
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
