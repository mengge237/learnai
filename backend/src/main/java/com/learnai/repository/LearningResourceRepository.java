package com.learnai.repository;

import com.learnai.entity.LearningResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
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

    /** 审核历史：已审核（有审核时间）的记录，按审核时间倒序 */
    Page<LearningResource> findByApprovedDateIsNotNullOrderByApprovedDateDesc(Pageable pageable);

    long countByApprovedDateIsNotNull();

    long countByApprovedDateAfter(LocalDateTime after);

    boolean existsByCategoryCategoryId(Long categoryId);

    long countByCategoryCategoryId(Long categoryId);

    /** 全局搜索：标题/简介/作者模糊匹配（仅公开且已审核，按热度排序） */
    @Query("select r from LearningResource r where r.isApproved = true and r.isPublic = true " +
            "and (lower(r.resourceTitle) like lower(concat('%', :kw, '%')) " +
            "or lower(r.description) like lower(concat('%', :kw, '%')) " +
            "or lower(r.author) like lower(concat('%', :kw, '%'))) " +
            "order by r.likeCount desc, r.viewCount desc")
    Page<LearningResource> searchPublic(String kw, Pageable pageable);

    /** 分类统计：按分类聚合公开资源数（分组后由 Service 沿树向上汇总） */
    @Query("select r.category.categoryId, count(r) from LearningResource r " +
            "where r.isApproved = true and r.isPublic = true and r.category is not null " +
            "group by r.category.categoryId")
    List<Object[]> countPublicByCategory();
}
