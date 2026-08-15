package com.learnai.repository;

import com.learnai.entity.AiInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiInteractionRepository extends JpaRepository<AiInteraction, Long> {

    List<AiInteraction> findTop50ByUserIdOrderByInteractionTimeDesc(Long userId);

    long countByUserId(Long userId);
}
