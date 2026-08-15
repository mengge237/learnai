package com.learnai.service;

import com.learnai.dto.ai.AiHistoryItemDto;
import com.learnai.dto.ai.AnalyticsDto;
import com.learnai.dto.ai.ChatRequest;
import com.learnai.dto.ai.ChatResponse;
import com.learnai.dto.ai.RecommendResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.entity.AiInteraction;
import com.learnai.entity.LearningPath;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.enums.LearningStatus;
import com.learnai.repository.AiInteractionRepository;
import com.learnai.repository.LearningPathRepository;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import lombok.RequiredArgsConstructor;
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
 * AI 学习助手：规则式对话（匹配顺序与文案沿用旧系统）、智能推荐、学习分析
 */
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
    private final LearningPathRepository pathRepository;
    private final LearningRecordRepository recordRepository;

    /** 对话：规则匹配生成回复并记录历史 */
    @Transactional
    public ChatResponse chat(Long userId, ChatRequest req) {
        String aiMessage = generateResponse(req.message(), req.resourceId(), userId);
        AiInteraction interaction = new AiInteraction();
        interaction.setUserId(userId);
        interaction.setResourceId(req.resourceId());
        interaction.setUserMessage(req.message());
        interaction.setAiMessage(aiMessage);
        interaction.setInteractionType("chat");
        interaction.setTopic(extractTopic(req.message()));
        interaction.setInteractionTime(LocalDateTime.now());
        interactionRepository.save(interaction);
        return new ChatResponse(req.message(), aiMessage, interaction.getInteractionTime());
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

    // ---------- 规则回复（匹配顺序与文案照抄旧系统） ----------

    private String generateResponse(String message, Long resourceId, Long userId) {
        String msg = message == null ? "" : message.toLowerCase().trim();

        if (msg.contains("你好") || msg.contains("hi") || msg.contains("hello")) {
            return "你好！我是您的AI学习助手。请问有什么我可以帮助您的吗？无论是学习上的问题，还是需要推荐学习资源，我都很乐意为您服务！";
        }
        if (msg.contains("推荐") || msg.contains("建议")) {
            return resourceRecommendations();
        }
        if (msg.contains("学习路径") || msg.contains("学习计划")) {
            return pathRecommendations();
        }
        if (msg.contains("进度") || msg.contains("学习进度")) {
            return progressReport(userId);
        }
        if (msg.contains("问题") || msg.contains("疑问") || msg.contains("不懂")) {
            return "好的，让我来帮您解答这个问题。由于这是一个模拟AI助手，我无法实时回答具体的技术问题。建议您查看相关学习资源的详细内容，或者在学习社区中提问。如果您有关于学习方法或学习规划的问题，我很乐意提供建议！";
        }
        if (msg.contains("谢谢") || msg.contains("感谢")) {
            return "不客气！祝您学习愉快！如果还有其他问题，随时可以来找我。";
        }
        if (msg.contains("再见") || msg.contains("拜拜")) {
            return "再见！祝您学习进步，下次见！";
        }
        if (resourceId != null) {
            return resourceRepository.findById(resourceId)
                    .map(r -> "关于「" + r.getResourceTitle() + "」这个学习资源，我可以帮您：\n\n"
                            + "1. 解释学习重点和难点\n2. 提供学习方法建议\n3. 推荐相关学习资源\n4. 帮助制定学习计划\n\n"
                            + "请问您想了解哪方面的内容呢？")
                    .orElseGet(this::fallback);
        }
        return fallback();
    }

    private String fallback() {
        return "我理解您的问题，但作为AI学习助手，我的能力主要集中在学习指导方面。请问您有关于学习资源、学习方法或学习规划的问题吗？我很乐意帮助您！";
    }

    private String resourceRecommendations() {
        List<LearningResource> top = resourceRepository.findPopular(5);
        if (top.isEmpty()) {
            return "目前没有找到合适的学习资源推荐。请尝试搜索特定的学习主题。";
        }
        StringBuilder sb = new StringBuilder("根据您的学习需求，我为您推荐以下热门学习资源：\n\n");
        int index = 1;
        for (LearningResource r : top) {
            sb.append(index++).append(". 《").append(r.getResourceTitle()).append("》\n")
                    .append("   - 难度：").append(r.getDifficultyLevel() == null ? "未知" : r.getDifficultyLevel()).append("\n")
                    .append("   - 时长：").append(r.getDurationMinutes() == null ? 0 : r.getDurationMinutes()).append("分钟\n")
                    .append("   - 作者：").append(r.getAuthor() == null ? "平台" : r.getAuthor()).append("\n\n");
        }
        sb.append("点击资源名称即可开始学习！");
        return sb.toString();
    }

    private String pathRecommendations() {
        List<LearningPath> paths = pathRepository.findByIsActiveTrueOrderByEnrollmentCountDesc()
                .stream().limit(3).toList();
        if (paths.isEmpty()) {
            return "目前还没有学习路径。您可以浏览学习资源，根据自己的兴趣制定学习计划。";
        }
        StringBuilder sb = new StringBuilder("为您推荐以下热门学习路径：\n\n");
        int index = 1;
        for (LearningPath p : paths) {
            sb.append(index++).append(". 《").append(p.getPathName()).append("》\n")
                    .append("   - 适合人群：").append(p.getTargetAudience() == null ? "所有人" : p.getTargetAudience()).append("\n")
                    .append("   - 预计时长：").append(p.getEstimatedHours() == null ? 0 : p.getEstimatedHours()).append("小时\n")
                    .append("   - 难度等级：").append(p.getDifficultyLevel()).append("级\n")
                    .append("   - 已有 ").append(p.getEnrollmentCount()).append(" 人报名\n\n");
        }
        sb.append("点击学习路径名称即可查看详情并报名！");
        return sb.toString();
    }

    private String progressReport(Long userId) {
        List<LearningRecord> records = recordRepository.findByUserId(userId);
        if (records.isEmpty()) {
            return "您还没有开始任何学习。点击首页的学习资源开始您的学习之旅吧！";
        }
        int completedCount = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.Completed).count();
        int inProgressCount = (int) records.stream().filter(r -> r.getStatus() == LearningStatus.InProgress).count();
        double avgProgress = records.stream()
                .mapToDouble(r -> r.getProgress() == null ? 0 : r.getProgress()).average().orElse(0);
        Map<Long, LearningResource> resources = resourceRepository.findAllById(
                        records.stream().map(LearningRecord::getResourceId).toList())
                .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));

        StringBuilder sb = new StringBuilder("您的学习进度报告：\n\n");
        sb.append("📚 总学习资源数：").append(records.size()).append("\n");
        sb.append("✅ 已完成：").append(completedCount).append("\n");
        sb.append("🔄 学习中：").append(inProgressCount).append("\n");
        sb.append(String.format("📊 平均进度：%.1f%%%n%n", avgProgress));
        sb.append("最近学习的资源：\n");
        records.stream()
                .sorted(Comparator.comparing(LearningRecord::getStartTime, Comparator.reverseOrder()))
                .limit(3)
                .forEach(rec -> {
                    LearningResource r = resources.get(rec.getResourceId());
                    String statusText = rec.getStatus() == LearningStatus.Completed ? "已完成"
                            : rec.getStatus() == LearningStatus.InProgress
                                    ? String.format("进行中 (%.0f%%)", rec.getProgress() == null ? 0 : rec.getProgress())
                                    : "未开始";
                    sb.append("  - 《").append(r == null ? "未知" : r.getResourceTitle()).append("》: ")
                            .append(statusText).append("\n");
                });
        sb.append("\n继续加油！坚持学习，您会取得更大的进步！");
        return sb.toString();
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
