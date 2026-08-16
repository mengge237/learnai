package com.learnai.repository;

import com.learnai.entity.LearningStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LearningStepRepository extends JpaRepository<LearningStep, Long> {

    List<LearningStep> findByRecordIdOrderByStepNumberAsc(Long recordId);

    Optional<LearningStep> findByRecordIdAndStepNumber(Long recordId, Integer stepNumber);

    /** 某资源所有学习记录下的全部步骤（跨记录联查，供教程内容同步） */
    @Query("select s from LearningStep s, LearningRecord r where s.recordId = r.recordId and r.resourceId = :resourceId")
    List<LearningStep> findByResourceId(@Param("resourceId") Long resourceId);

    void deleteByRecordId(Long recordId);
}
