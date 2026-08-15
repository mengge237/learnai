package com.learnai.repository;

import com.learnai.entity.LearningPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByIsActiveTrueOrderByEnrollmentCountDesc();

    Page<LearningPath> findByIsActiveTrue(Pageable pageable);

    /** 全局搜索：路径名/简介模糊匹配（启用中，按报名数排序） */
    @Query("select p from LearningPath p where p.isActive = true " +
            "and (lower(p.pathName) like lower(concat('%', :kw, '%')) " +
            "or lower(p.description) like lower(concat('%', :kw, '%'))) " +
            "order by p.enrollmentCount desc")
    Page<LearningPath> searchActive(String kw, Pageable pageable);
}
