package com.learnai.dto.ai;

import com.learnai.dto.learning.ResourceDto;

import java.util.List;

/**
 * 智能推荐响应（basedOn: popular / history / category:{分类名}）
 */
public record RecommendResponse(
        List<ResourceDto> recommendations,
        String basedOn
) {
}
