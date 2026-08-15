package com.learnai.service.ai;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 真实大模型答疑（OpenAI 兼容协议）：DeepSeek / 通义千问 / OpenAI 等任何兼容
 * /chat/completions 接口的服务均可配置接入。调用失败由 AiService 回退规则式。
 */
@Component
public class LlmAiProvider implements AiProvider {

    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final RestClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LlmAiProvider(
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.base-url:https://api.deepseek.com}") String baseUrl,
            @Value("${app.ai.model:deepseek-chat}") String model) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.baseUrl = (baseUrl == null ? "" : baseUrl.trim()).replaceAll("/+$", "");
        this.model = model == null ? "deepseek-chat" : model.trim();
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(60_000);
        this.client = RestClient.builder().requestFactory(factory).build();
    }

    /** 是否已配置真实大模型（无 Key 时由工厂选择规则式演示） */
    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    @Override
    public String reply(ChatContext ctx) {
        ObjectNode body = mapper.createObjectNode();
        body.put("model", model);
        body.put("temperature", 0.6);
        body.put("max_tokens", 1000);

        ArrayNode messages = body.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", buildSystemPrompt(ctx));
        messages.addObject()
                .put("role", "user")
                .put("content", ctx.message() == null ? "" : ctx.message());

        String resp = client.post()
                .uri(baseUrl + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.toString())
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = mapper.readTree(resp);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new IllegalStateException("大模型返回内容为空");
            }
            return content.asText().trim();
        } catch (Exception e) {
            throw new IllegalStateException("解析大模型响应失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String name() {
        return "llm";
    }

    /** 人设 + 用户学习概况注入，让回答有针对性 */
    private String buildSystemPrompt(ChatContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是「AI智学」校园学习平台的在线学习助手，负责解答 3D 建模、图形学、前端开发等领域的学习问题，");
        sb.append("为同学推荐学习资源、分析学习进度、制定学习计划。\n");
        sb.append("回答要求：使用简体中文；简洁有条理，多用编号列表；只回答与学习相关的内容；");
        sb.append("不编造平台中不存在的课程或资源。\n");
        if (ctx.progressSummary() != null && !ctx.progressSummary().isBlank()) {
            sb.append("当前同学的学习概况：\n").append(ctx.progressSummary()).append("\n");
        }
        if (ctx.resourceTitle() != null && !ctx.resourceTitle().isBlank()) {
            sb.append("该同学当前正在学习《").append(ctx.resourceTitle()).append("》，可优先结合此内容回答。\n");
        }
        return sb.toString();
    }
}
