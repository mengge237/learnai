package com.learnai.dto.search;

import java.util.List;

/**
 * 全局搜索响应：按学习资源 / 学习路径 / 3D 模型三组返回（每组前若干条 + 命中总数）
 */
public record GlobalSearchResponse(
        List<SearchItemDto> resources,
        long resourceTotal,
        List<SearchItemDto> paths,
        long pathTotal,
        List<SearchItemDto> models,
        long modelTotal
) {
}
