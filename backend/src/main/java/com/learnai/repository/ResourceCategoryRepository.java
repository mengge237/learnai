package com.learnai.repository;

import com.learnai.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {

    List<ResourceCategory> findByIsActiveTrueOrderBySortOrderAsc();

    /** 某父分类下的子分类（用于父分类筛选时扩展到全部子分类资源） */
    List<ResourceCategory> findByIsActiveTrueAndParentCategoryIdOrderBySortOrderAsc(Long parentCategoryId);

    boolean existsByParentCategoryId(Long parentCategoryId);
}
