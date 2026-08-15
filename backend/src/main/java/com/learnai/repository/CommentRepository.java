package com.learnai.repository;

import com.learnai.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByResourceIdAndIsApprovedTrueOrderByCommentDateAsc(Long resourceId);

    List<Comment> findByModelIdAndIsApprovedTrueOrderByCommentDateAsc(Long modelId);
}
