package com.learnai.repository;

import com.learnai.entity.LearningStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningStepRepository extends JpaRepository<LearningStep, Long> {

    List<LearningStep> findByRecordIdOrderByStepNumberAsc(Long recordId);

    void deleteByRecordId(Long recordId);
}
