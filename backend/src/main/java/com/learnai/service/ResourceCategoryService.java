package com.learnai.service;

import com.learnai.dto.learning.CategoryNodeDto;
import com.learnai.repository.ResourceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceCategoryService {

    private final ResourceCategoryRepository categoryRepository;

    /** 分类树（parentCategoryId 为空的是根节点） */
    @Transactional(readOnly = true)
    public List<CategoryNodeDto> tree() {
        List<CategoryNodeDto> all = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(CategoryNodeDto::from)
                .collect(Collectors.toList());

        Map<Long, CategoryNodeDto> byId = all.stream()
                .collect(Collectors.toMap(CategoryNodeDto::id, c -> c));

        List<CategoryNodeDto> roots = new ArrayList<>();
        for (CategoryNodeDto node : all) {
            if (node.parentId() != null && byId.containsKey(node.parentId())) {
                byId.get(node.parentId()).children().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }
}
