package com.learnai.service.factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 教程步骤模板工厂（工厂模式 + 模板注册表统一管理）。
 *
 * <p>所有课程的默认步骤标题与正文模板集中在此处注册，运行期建步骤
 * （LearningProgressService）与种子数据（DataInitializer）共用同一份模板，
 * 保证「模板只有一处定义」，新增/修改步骤只需改动本类。</p>
 */
public final class StepTemplateFactory {

    /** 步骤模板：标题 + 正文模板（%s 占位符在渲染时替换为资源名） */
    public record StepTemplate(String title, String contentTemplate) {
        /** 渲染正文：把 %s 替换为资源标题 */
        public String render(String resourceTitle) {
            return String.format(contentTemplate, resourceTitle);
        }
    }

    /** 模板注册表：步骤序号 → 模板（按序号有序） */
    private static final Map<Integer, StepTemplate> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put(1, new StepTemplate(
                "了解内容概览",
                "欢迎开始《%s》的学习！本步骤先带你建立整体认识。\n\n"
                        + "▍课程定位\n《%s》面向零基础入门到进阶的学习者，围绕核心概念与常用工具展开，"
                        + "配套演示模型与练习素材，边学边练。\n\n"
                        + "▍知识地图\n本课程共分为三个步骤：先了解概览 → 再深入学习与实践 → 最后完成练习与总结。"
                        + "建议按顺序完成，每一步都会解锁下一步的内容。\n\n"
                        + "▍学习建议\n阅读时随手做笔记；遇到不懂的概念，可以点击右下角的答疑入口即时提问。"));
        REGISTRY.put(2, new StepTemplate(
                "深入学习与实践",
                "本步骤是《%s》的核心内容，请跟随示例动手实践。\n\n"
                        + "▍核心要点\n1. 理解基础概念：先弄清「是什么」和「为什么」；\n"
                        + "2. 跟随示例操作：打开配套演示模型，一步步复现；\n"
                        + "3. 独立练习：脱离示例再操作一遍，检验掌握程度。\n\n"
                        + "▍常见问题\n- 操作卡住时，先查看上文步骤是否遗漏；\n"
                        + "- 报错信息是重要的线索，不要忽略；\n"
                        + "- 仍然解决不了？随时发起提问，帮你分析。\n\n"
                        + "▍实践任务\n完成一次完整的操作流程并截图保存，作为自己的学习成果。"));
        REGISTRY.put(3, new StepTemplate(
                "完成练习与总结",
                "恭喜来到《%s》的最后一步！用练习检验成果，用总结沉淀知识。\n\n"
                        + "▍结课练习\n1. 独立完成本课程的实践任务；\n"
                        + "2. 用自己的话复述三个核心知识点；\n"
                        + "3. 对照学习目标检查是否全部达成。\n\n"
                        + "▍学习总结\n建议写下：本次学习的收获、遇到的难点、还想深入的方向。"
                        + "总结会保存在学习记录里，方便日后回顾。\n\n"
                        + "▍提交完成\n全部步骤完成后，点击「提交完成」为本次学习打分，"
                        + "系统会记录你的学习成就并计入学习分析。"));
    }

    private StepTemplateFactory() {
        // 工具类：禁止实例化
    }

    /** 步骤总数 */
    public static int size() {
        return REGISTRY.size();
    }

    /** 按序号取模板（不存在返回 null） */
    public static StepTemplate templateOf(int stepNumber) {
        return REGISTRY.get(stepNumber);
    }

    /** 全部模板（按序号升序） */
    public static List<StepTemplate> all() {
        return List.copyOf(REGISTRY.values());
    }
}
