package com.learnai.service;

import com.learnai.dto.learning.CategoryAdminDto;
import com.learnai.dto.learning.CategoryNodeDto;
import com.learnai.dto.learning.CategorySaveRequest;
import com.learnai.entity.ResourceCategory;
import com.learnai.exception.ApiException;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.ResourceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

    // ---------- 管理端：分类 CRUD ----------

    /** 全部分类（含停用），带该分类下的资源数 */
    @Transactional(readOnly = true)
    public List<CategoryAdminDto> adminList() {
        Map<Long, Integer> direct = new HashMap<>();
        for (Object[] row : resourceRepository.countPublicByCategory()) {
            direct.put((Long) row[0], ((Long) row[1]).intValue());
        }
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder")).stream()
                .map(c -> new CategoryAdminDto(c.getCategoryId(), c.getCategoryName(), c.getDescription(),
                        c.getParentCategoryId(), c.getSortOrder(), c.getIsActive(),
                        direct.getOrDefault(c.getCategoryId(), 0)))
                .toList();
    }

    @Transactional
    public CategoryAdminDto create(CategorySaveRequest req) {
        validateParent(null, req.parentId());
        ResourceCategory c = new ResourceCategory();
        apply(c, req);
        return toAdminDto(categoryRepository.save(c));
    }

    @Transactional
    public CategoryAdminDto update(Long id, CategorySaveRequest req) {
        ResourceCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("分类不存在"));
        validateParent(id, req.parentId());
        apply(c, req);
        return toAdminDto(c);
    }

    @Transactional
    public void delete(Long id) {
        ResourceCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("分类不存在"));
        if (categoryRepository.existsByParentCategoryId(id)) {
            throw ApiException.badRequest("该分类下存在子分类，无法删除");
        }
        if (resourceRepository.existsByCategoryCategoryId(id)) {
            throw ApiException.badRequest("该分类下存在学习资源，无法删除");
        }
        categoryRepository.delete(c);
    }

    private void validateParent(Long selfId, Long parentId) {
        if (parentId == null) {
            return;
        }
        if (parentId.equals(selfId)) {
            throw ApiException.badRequest("不能把自己设为上级分类");
        }
        if (!categoryRepository.existsById(parentId)) {
            throw ApiException.badRequest("上级分类不存在");
        }
    }

    private void apply(ResourceCategory c, CategorySaveRequest req) {
        c.setCategoryName(req.name().trim());
        c.setDescription(req.description());
        c.setParentCategoryId(req.parentId());
        c.setSortOrder(req.sortOrder());
        c.setIsActive(req.isActive());
    }

    private CategoryAdminDto toAdminDto(ResourceCategory c) {
        return new CategoryAdminDto(c.getCategoryId(), c.getCategoryName(), c.getDescription(),
                c.getParentCategoryId(), c.getSortOrder(), c.getIsActive(),
                resourceRepository.countByCategoryCategoryId(c.getCategoryId()));
    }
}
