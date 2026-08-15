package com.learnai.dto.search;

/**
 * 全局搜索命中项（资源/路径/模型通用扁平结构，前端按 type 生成跳转链接）
 */
public record SearchItemDto(
        Long id,
        String type,        // resource / path / model
        String title,
        String description,
        String coverUrl,
        String meta         // 辅助信息：资源=分类·作者，路径=难度·时长，模型=作者
) {
}
