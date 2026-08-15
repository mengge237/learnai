package com.learnai.service;

import com.learnai.dto.learning.CategoryNodeDto;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.ResourceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceCategoryService {

    private final ResourceCategoryRepository categoryRepository;
    private final LearningResourceRepository resourceRepository;

    /** 分类树（parentCategoryId 为空的是根节点），每个节点带公开资源数（含子分类合计） */
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

        // 公开资源数：一次分组查询得到叶子分类计数，再沿树向上汇总
        Map<Long, Integer> direct = new HashMap<>();
        for (Object[] row : resourceRepository.countPublicByCategory()) {
            direct.put((Long) row[0], ((Long) row[1]).intValue());
        }
        Map<Long, Integer> totals = new HashMap<>();
        roots.forEach(root -> accumulate(root, direct, totals));

        return roots.stream().map(node -> withCount(node, totals)).toList();
    }

    /** 递归累加子树资源数 */
    private int accumulate(CategoryNodeDto node, Map<Long, Integer> direct, Map<Long, Integer> totals) {
        int sum = direct.getOrDefault(node.id(), 0);
        for (CategoryNodeDto child : node.children()) {
            sum += accumulate(child, direct, totals);
        }
        totals.put(node.id(), sum);
        return sum;
    }

    /** 重建节点并写入统计数 */
    private CategoryNodeDto withCount(CategoryNodeDto node, Map<Long, Integer> totals) {
        return new CategoryNodeDto(node.id(), node.name(), node.description(), node.parentId(), node.sortOrder(),
                totals.getOrDefault(node.id(), 0),
                node.children().stream().map(c -> withCount(c, totals)).toList());
    }
}
