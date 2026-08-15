package com.learnai.repository;

import com.learnai.entity.UserLearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLearningPathRepository extends JpaRepository<UserLearningPath, Long> {

    Optional<UserLearningPath> findByUserIdAndPathId(Long userId, Long pathId);

    List<UserLearningPath> findByUserIdOrderByEnrollDateDesc(Long userId);
}
