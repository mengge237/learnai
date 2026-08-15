package com.learnai.service;

import com.learnai.dto.search.GlobalSearchResponse;
import com.learnai.dto.search.SearchItemDto;
import com.learnai.entity.LearningPath;
import com.learnai.entity.LearningResource;
import com.learnai.entity.Model3D;
import com.learnai.repository.LearningPathRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.Model3DRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

/**
 * 全局搜索：跨学习资源、学习路径、3D 模型三类内容的关键词检索
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    /** 每组最多返回条数（更多走各列表页的搜索） */
    private static final int TOP = 6;

    private final LearningResourceRepository resourceRepository;
    private final LearningPathRepository pathRepository;
    private final Model3DRepository modelRepository;

    @Transactional(readOnly = true)
    public GlobalSearchResponse search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        if (kw.isEmpty()) {
            return new GlobalSearchResponse(List.of(), 0, List.of(), 0, List.of(), 0);
        }
        PageRequest top = PageRequest.of(0, TOP);

        Page<LearningResource> resources = resourceRepository.searchPublic(kw, top);
        Page<LearningPath> paths = pathRepository.searchActive(kw, top);
        Page<Model3D> models = modelRepository.searchPublic(kw, top);

        return new GlobalSearchResponse(
                mapGroup(resources, r -> new SearchItemDto(
                        r.getResourceId(), "resource", r.getResourceTitle(), r.getDescription(),
                        r.getThumbnailUrl(),
                        (r.getCategory() == null ? "" : r.getCategory().getCategoryName() + " · ")
                                + (r.getAuthor() == null ? "平台" : r.getAuthor()))),
                resources.getTotalElements(),
                mapGroup(paths, p -> new SearchItemDto(
                        p.getPathId(), "path", p.getPathName(), p.getDescription(), p.getCoverImageUrl(),
                        "难度 " + p.getDifficultyLevel() + " 级 · 约 " + p.getEstimatedHours() + " 小时")),
                paths.getTotalElements(),
                mapGroup(models, m -> new SearchItemDto(
                        m.getModelId(), "model", m.getModelName(),
                        (m.getCreator() == null ? "" : m.getCreator() + " · ") + "3D 模型资源",
                        m.getPreviewUrl(), m.getCreator() == null ? "" : "作者 " + m.getCreator())),
                models.getTotalElements());
    }

    private static <T> List<SearchItemDto> mapGroup(Page<T> page, Function<T, SearchItemDto> mapper) {
        return page.getContent().stream().map(mapper).toList();
    }
}
