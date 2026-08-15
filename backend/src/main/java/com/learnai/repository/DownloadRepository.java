package com.learnai.repository;

import com.learnai.entity.Download;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadRepository extends JpaRepository<Download, Long> {

    List<Download> findByUserIdOrderByDownloadTimeDesc(Long userId);

    long countByUserId(Long userId);
}
