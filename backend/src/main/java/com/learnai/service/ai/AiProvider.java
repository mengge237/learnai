package com.learnai.service.ai;

/**
 * 答疑回复策略接口（策略模式）：真实大模型 / 规则式演示 两种实现可插拔。
 * 由 {@link AiProviderFactory} 按配置选择，LLM 调用失败时自动回退规则式。
 */
public interface AiProvider {

    /** 生成回复；ChatContext 含用户学习概况，供大模型作为上下文 */
    String reply(ChatContext ctx);

    /** 提供方标识（llm = 真实大模型，rule = 规则式演示） */
    String name();
}
