package com.learnai.repository;

import com.learnai.entity.Model3D;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface Model3DRepository extends JpaRepository<Model3D, Long>, JpaSpecificationExecutor<Model3D> {

    long countByIsPublicTrue();

    long countByIsApprovedFalseAndRejectionReasonIsNull();

    List<Model3D> findByIsApprovedFalseAndRejectionReasonIsNullOrderByCreateDateDesc();

    /** 审核历史：已审核（有审核时间）的记录，按审核时间倒序 */
    Page<Model3D> findByApprovedDateIsNotNullOrderByApprovedDateDesc(Pageable pageable);

    long countByApprovedDateIsNotNull();

    long countByApprovedDateAfter(LocalDateTime after);

    /** 全局搜索：模型名/作者模糊匹配（仅公开且已审核） */
    @Query("select m from Model3D m where m.isApproved = true and m.isPublic = true " +
            "and (lower(m.modelName) like lower(concat('%', :kw, '%')) " +
            "or lower(m.creator) like lower(concat('%', :kw, '%'))) " +
            "order by m.createDate desc")
    Page<Model3D> searchPublic(String kw, Pageable pageable);
}
