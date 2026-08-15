package com.learnai.repository;

import com.learnai.entity.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

    Optional<StudyLog> findByUserIdAndStudyDate(Long userId, LocalDate studyDate);

    List<StudyLog> findByUserIdAndStudyDateGreaterThanEqualOrderByStudyDateAsc(Long userId, LocalDate from);

    List<StudyLog> findByUserIdOrderByStudyDateDesc(Long userId);
}
