package com.learnai.service.ai;

import com.learnai.entity.LearningPath;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.enums.LearningStatus;
import com.learnai.repository.LearningPathRepository;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规则式答疑（演示模式兜底）：关键词匹配 + 平台数据生成回复，匹配顺序与文案沿用旧系统。
 * 配置了大模型 API Key 时被 {@link LlmAiProvider} 取代，仅作失败回退。
 */
@Component
@RequiredArgsConstructor
public class RuleBasedAiProvider implements AiProvider {

    private final LearningResourceRepository resourceRepository;
    private final LearningPathRepository pathRepository;
    private final LearningRecordRepository recordRepository;

    @Override
    public String reply(ChatContext ctx) {
        String msg = ctx.message() == null ? "" : ctx.message().toLowerCase().trim();

        if (msg.contains("你好") || msg.contains("hi") || msg.contains("hello")) {
            return "你好！我是您的学习助手。请问有什么我可以帮助您的吗？无论是学习上的问题，还是需要推荐学习资源，我都很乐意为您服务！";
        }
        if (msg.contains("推荐") || msg.contains("建议")) {
            return resourceRecommendations();
        }
        if (msg.contains("学习路径") || msg.contains("学习计划")) {
            return pathRecommendations();
        }
        if (msg.contains("进度") || msg.contains("学习进度")) {
            return progressReport(ctx.userId());
        }
        if (msg.contains("问题") || msg.contains("疑问") || msg.contains("不懂")) {
            return "好的，让我来帮您解答这个问题。当前为演示模式（未配置大模型接口），我无法实时回答具体的技术问题。建议您查看相关学习资源的详细内容，或者在学习社区中提问。如果您有关于学习方法或学习规划的问题，我很乐意提供建议！";
        }
        if (msg.contains("谢谢") || msg.contains("感谢")) {
            return "不客气！祝您学习愉快！如果还有其他问题，随时可以来找我。";
        }
        if (msg.contains("再见") || msg.contains("拜拜")) {
            return "再见！祝您学习进步，下次见！";
        }
        if (ctx.resourceId() != null) {
            return resourceRepository.findById(ctx.resourceId())
                    .map(r -> "关于「" + r.getResourceTitle() + "」这个学习资源，我可以帮您：\n\n"
                            + "1. 解释学习重点和难点\n2. 提供学习方法建议\n3. 推荐相关学习资源\n4. 帮助制定学习计划\n\n"
                            + "请问您想了解哪方面的内容呢？")
                    .orElseGet(this::fallback);
        }
        return fallback();
    }

    @Override
    public String name() {
        return "rule";
    }

    private String fallback() {
        return "我理解您的问题，但作为学习助手，我的能力主要集中在学习指导方面。请问您有关于学习资源、学习方法或学习规划的问题吗？我很乐意帮助您！";
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
        sb.append("总学习资源数：").append(records.size()).append("\n");
        sb.append("已完成：").append(completedCount).append("\n");
        sb.append("学习中：").append(inProgressCount).append("\n");
        sb.append(String.format("平均进度：%.1f%%%n%n", avgProgress));
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
}
