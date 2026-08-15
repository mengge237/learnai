package com.learnai.repository;

import com.learnai.entity.LearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningRecordRepository extends JpaRepository<LearningRecord, Long> {

    Optional<LearningRecord> findByUserIdAndResourceId(Long userId, Long resourceId);

    List<LearningRecord> findByUserIdOrderByStartTimeDesc(Long userId);

    List<LearningRecord> findByUserId(Long userId);
}
