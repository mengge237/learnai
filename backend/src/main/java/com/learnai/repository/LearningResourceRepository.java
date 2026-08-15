package com.learnai.repository;

import com.learnai.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long>, JpaSpecificationExecutor<LearningResource> {

    long countByIsPublicTrue();

    long countByIsApprovedFalseAndRejectionReasonIsNull();

    @Query("select r from LearningResource r where r.isPublic = true and r.isApproved = true " +
            "order by r.likeCount desc, r.viewCount desc limit :limit")
    List<LearningResource> findPopular(int limit);

    /** 已完成分类中未学过的资源（按点赞数） */
    List<LearningResource> findTop6ByIsApprovedTrueAndIsPublicTrueAndCategoryCategoryIdInAndResourceIdNotInOrderByLikeCountDesc(
            List<Long> categoryIds, List<Long> excludeIds);

    List<LearningResource> findByIsApprovedFalseAndRejectionReasonIsNullOrderByCreateDateDesc();
}
