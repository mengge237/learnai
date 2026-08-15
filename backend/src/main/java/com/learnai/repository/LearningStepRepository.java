package com.learnai.repository;

import com.learnai.entity.LearningStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningStepRepository extends JpaRepository<LearningStep, Long> {

    List<LearningStep> findByRecordIdOrderByStepNumberAsc(Long recordId);

    Optional<LearningStep> findByRecordIdAndStepNumber(Long recordId, Integer stepNumber);

    void deleteByRecordId(Long recordId);
}
