package com.learnai.repository;

import com.learnai.entity.PathResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PathResourceRepository extends JpaRepository<PathResource, Long> {

    List<PathResource> findByPathIdOrderBySequenceNumberAsc(Long pathId);

    long countByPathId(Long pathId);

    void deleteByPathId(Long pathId);
}
