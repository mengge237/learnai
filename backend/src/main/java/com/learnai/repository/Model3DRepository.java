package com.learnai.repository;

import com.learnai.entity.Model3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface Model3DRepository extends JpaRepository<Model3D, Long>, JpaSpecificationExecutor<Model3D> {

    long countByIsPublicTrue();

    long countByIsApprovedFalseAndRejectionReasonIsNull();

    List<Model3D> findByIsApprovedFalseAndRejectionReasonIsNullOrderByCreateDateDesc();
}
