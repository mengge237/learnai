package com.learnai.repository;

import com.learnai.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByIsActiveTrueOrderByEnrollmentCountDesc();
}
