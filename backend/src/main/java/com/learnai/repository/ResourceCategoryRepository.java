package com.learnai.repository;

import com.learnai.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {

    List<ResourceCategory> findByIsActiveTrueOrderBySortOrderAsc();
}
