package com.learnai.service;

import com.learnai.dto.ai.AiHistoryItemDto;
import com.learnai.dto.ai.AnalyticsDto;
import com.learnai.dto.ai.ChatRequest;
import com.learnai.dto.ai.ChatResponse;
import com.learnai.dto.ai.RecommendResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.entity.AiInteraction;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.enums.LearningStatus;
import com.learnai.repository.AiInteractionRepository;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.service.ai.AiProvider;
import com.learnai.service.ai.AiProviderFactory;
import com.learnai.service.ai.ChatContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学习答疑助手：策略模式接入答疑提供方（真实大模型 / 规则式兜底）、智能推荐、学习分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private static final Map<DayOfWeek, String> DAY_NAMES = Map.of(
            DayOfWeek.MONDAY, "星期一",
            DayOfWeek.TUESDAY, "星期二",
            DayOfWeek.WEDNESDAY, "星期三",
            DayOfWeek.THURSDAY, "星期四",
            DayOfWeek.FRIDAY, "星期五",
            DayOfWeek.SATURDAY, "星期六",
            DayOfWeek.SUNDAY, "星期日");

    private final AiInteractionRepository interactionRepository;
    private final LearningResourceRepository resourceRepository;
    private final LearningRecordRepository recordRepository;
    private final AiProviderFactory providerFactory;

    /** 对话：按配置选择答疑提供方（LLM 优先，失败回退规则式）并记录历史 */
    @Transactional
    public ChatResponse chat(Long userId, ChatRequest req) {
        ChatContext ctx = buildContext(userId, req.message(), req.resourceId());

        AiProvider provider = providerFactory.get();
        String aiMessage;
        String providerName = provider.name();
        if ("llm".equals(providerName)) {
            try {
                aiMessage = provider.reply(ctx);
            } catch (Exception e) {
                log.warn("大模型调用失败，回退规则式答疑: {}", e.getMessage());
                aiMessage = providerFactory.ruleFallback().reply(ctx);
                providerName = "rule";
            }
        } else {
            aiMessage = provider.reply(ctx);
        }

        AiInteraction interaction = new AiInteraction();
        interaction.setUserId(userId);
        interaction.setResourceId(req.resourceId());
        interaction.setUserMessage(req.message());
        interaction.setAiMessage(aiMessage);
        interaction.setInteractionType("chat");
        interaction.setTopic(extractTopic(req.message()));
        interaction.setInteractionTime(LocalDateTime.now());
        interactionRepository.save(interaction);
        return new ChatResponse(req.message(), aiMessage, interaction.getInteractionTime(), providerName);
    }

    /** 组装答疑上下文：关联资源标题 + 学习进度概况 */
    private ChatContext buildContext(Long userId, String message, Long resourceId) {
        String resourceTitle = null;
        if (resourceId != null) {
            resourceTitle = resourceRepository.findById(resourceId)
                    .map(LearningResource::getResourceTitle).orElse(null);
        }
        return new ChatContext(userId, message, resourceId, resourceTitle, buildProgressSummary(userId));
    }

    /** 学习进度文字概况（注入大模型上下文，让它回答"我的进度"时有真实数据） */
    private String buildProgressSummary(Long userId) {
        List<LearningRecord> records = recordRepository.findByUserId(userId);
        if (records.isEmpty()) {
            return "尚未开始学习任何课程。";
        }
        int completed = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.Completed).count();
        int inProgress = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.InProgress).count();
        double avg = records.stream()
                .mapToDouble(r -> r.getProgress() == null ? 0 : r.getProgress()).average().orElse(0);
        Map<Long, LearningResource> resources = resourceRepository.findAllById(
                        records.stream().map(LearningRecord::getResourceId).toList())
                .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
        List<String> recent = records.stream()
                .sorted(Comparator.comparing(LearningRecord::getStartTime, Comparator.reverseOrder()))
                .limit(3)
                .map(rec -> {
                    String title = resources.containsKey(rec.getResourceId())
                            ? resources.get(rec.getResourceId()).getResourceTitle() : "未知课程";
                    String status = rec.getStatus() == LearningStatus.Completed ? "已完成"
                            : rec.getStatus() == LearningStatus.InProgress
                                    ? String.format("进行中 (%.0f%%)", rec.getProgress() == null ? 0 : rec.getProgress())
                                    : "未开始";
                    return String.format("《%s》%s", title, status);
                })
                .toList();
        return String.format("共学习 %d 门课程，已完成 %d 门，进行中 %d 门，平均进度 %.1f%%。最近学习：%s。",
                records.size(), completed, inProgress, avg, String.join("、", recent));
    }

    @Transactional(readOnly = true)
    public List<AiHistoryItemDto> history(Long userId) {
        return interactionRepository.findTop50ByUserIdOrderByInteractionTimeDesc(userId)
                .stream().map(AiHistoryItemDto::from).toList();
    }

    /** 智能推荐：无历史 → 热门；有历史 → 已完成分类的同类未学资源；否则回退热门 */
    @Transactional(readOnly = true)
    public RecommendResponse recommend(Long userId) {
        List<LearningRecord> records = recordRepository.findByUserId(userId);
        if (records.isEmpty()) {
            return new RecommendResponse(popular(6), "popular");
        }
        Set<Long> completedResourceIds = records.stream()
                .filter(r -> r.getStatus() == LearningStatus.Completed)
                .map(LearningRecord::getResourceId)
                .collect(Collectors.toSet());
        if (!completedResourceIds.isEmpty()) {
            Map<Long, LearningResource> completedResources = resourceRepository.findAllById(completedResourceIds)
                    .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
            Set<Long> categoryIds = completedResources.values().stream()
                    .filter(r -> r.getCategory() != null)
                    .map(r -> r.getCategory().getCategoryId())
                    .collect(Collectors.toSet());
            Set<Long> learnedIds = records.stream().map(LearningRecord::getResourceId).collect(Collectors.toSet());
            if (!categoryIds.isEmpty()) {
                List<LearningResource> byCategory = resourceRepository
                        .findTop6ByIsApprovedTrueAndIsPublicTrueAndCategoryCategoryIdInAndResourceIdNotInOrderByLikeCountDesc(
                                categoryIds.stream().toList(), learnedIds.stream().toList());
                if (!byCategory.isEmpty()) {
                    String catName = completedResources.values().stream()
                            .filter(r -> r.getCategory() != null && categoryIds.contains(r.getCategory().getCategoryId()))
                            .findFirst()
                            .map(r -> r.getCategory().getCategoryName())
                            .orElse("已完成分类");
                    return new RecommendResponse(
                            byCategory.stream().map(ResourceDto::from).toList(), "category:" + catName);
                }
            }
        }
        return new RecommendResponse(popular(6), "popular");
    }

    /** 学习分析总览 */
    @Transactional(readOnly = true)
    public AnalyticsDto analytics(Long userId) {
        List<LearningRecord> records = recordRepository.findByUserId(userId);
        long interactions = interactionRepository.countByUserId(userId);

        int completed = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.Completed).count();
        int inProgress = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.InProgress).count();
        double avg = records.stream()
                .mapToDouble(r -> r.getProgress() == null ? 0 : r.getProgress()).average().orElse(0);
        int minutes = records.stream()
                .mapToInt(r -> r.getDurationMinutes() == null ? 0 : r.getDurationMinutes()).sum();

        Map<Long, LearningResource> resources = records.isEmpty() ? Map.of()
                : resourceRepository.findAllById(records.stream().map(LearningRecord::getResourceId).toList())
                        .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));

        Map<String, List<LearningRecord>> byCategory = records.stream()
                .collect(Collectors.groupingBy(rec -> {
                    LearningResource r = resources.get(rec.getResourceId());
                    return r != null && r.getCategory() != null ? r.getCategory().getCategoryName() : "未分类";
                }));
        List<AnalyticsDto.CategoryStatDto> categoryStats = byCategory.entrySet().stream()
                .map(e -> new AnalyticsDto.CategoryStatDto(
                        e.getKey(),
                        e.getValue().size(),
                        (int) e.getValue().stream().filter(r -> r.getStatus() == LearningStatus.Completed).count(),
                        e.getValue().stream().mapToDouble(r -> r.getProgress() == null ? 0 : r.getProgress()).average().orElse(0)))
                .toList();

        LocalDate today = LocalDate.now();
        List<AnalyticsDto.WeeklyStatDto> weekly = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int dayRecords = (int) records.stream()
                    .filter(r -> r.getStartTime() != null && r.getStartTime().toLocalDate().equals(date)).count();
            int dayCompleted = (int) records.stream()
                    .filter(r -> r.getStatus() == LearningStatus.Completed
                            && r.getEndTime() != null && r.getEndTime().toLocalDate().equals(date)).count();
            weekly.add(new AnalyticsDto.WeeklyStatDto(date, DAY_NAMES.getOrDefault(date.getDayOfWeek(), ""),
                    dayRecords, dayCompleted));
        }

        List<AnalyticsDto.RecentRecordDto> recent = records.stream()
                .sorted(Comparator.comparing(LearningRecord::getStartTime, Comparator.reverseOrder()))
                .limit(5)
                .map(rec -> {
                    LearningResource r = resources.get(rec.getResourceId());
                    return new AnalyticsDto.RecentRecordDto(
                            rec.getResourceId(),
                            r == null ? null : r.getResourceTitle(),
                            rec.getStatus() == null ? null : rec.getStatus().name(),
                            rec.getProgress(), rec.getStartTime(), rec.getEndTime());
                })
                .toList();

        return new AnalyticsDto(records.size(), completed, inProgress, interactions,
                Math.round(avg * 10) / 10.0, minutes, categoryStats, weekly, recent);
    }

    private String extractTopic(String message) {
        if (message.contains("推荐")) {
            return "资源推荐";
        }
        if (message.contains("学习路径")) {
            return "学习路径";
        }
        if (message.contains("进度")) {
            return "学习进度";
        }
        if (message.contains("问题") || message.contains("疑问")) {
            return "问题解答";
        }
        return "普通对话";
    }

    private List<ResourceDto> popular(int limit) {
        return resourceRepository.findPopular(limit).stream().map(ResourceDto::from).toList();
    }
}
