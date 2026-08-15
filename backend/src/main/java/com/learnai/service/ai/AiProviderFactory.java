package com.learnai.service.ai;

import org.springframework.stereotype.Component;

/**
 * 答疑提供方工厂：按配置选择策略 ——
 * 配置了 app.ai.api-key 时使用真实大模型（{@link LlmAiProvider}），
 * 否则使用规则式演示（{@link RuleBasedAiProvider}）。
 * 大模型调用失败时由 AiService 回退 {@link #ruleFallback()}。
 */
@Component
public class AiProviderFactory {

    private final LlmAiProvider llm;
    private final RuleBasedAiProvider rule;

    public AiProviderFactory(LlmAiProvider llm, RuleBasedAiProvider rule) {
        this.llm = llm;
        this.rule = rule;
    }

    /** 当前生效的提供方 */
    public AiProvider get() {
        return llm.isConfigured() ? llm : rule;
    }

    /** 规则式兜底（LLM 异常时的降级策略） */
    public AiProvider ruleFallback() {
        return rule;
    }
}
