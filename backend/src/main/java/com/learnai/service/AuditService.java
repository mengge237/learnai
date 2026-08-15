package com.learnai.service;

import com.learnai.dto.audit.AuditRequest;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.dto.market.ModelDto;
import com.learnai.entity.LearningResource;
import com.learnai.entity.Model3D;
import com.learnai.exception.ApiException;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.Model3DRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核：资源/模型的待审核列表、通过、驳回
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final LearningResourceRepository resourceRepository;
    private final Model3DRepository modelRepository;

    @Transactional(readOnly = true)
    public List<ResourceDto> pendingResources() {
        return resourceRepository.findByIsApprovedFalseAndRejectionReasonIsNullOrderByCreateDateDesc()
                .stream().map(ResourceDto::from).toList();
    }

    @Transactional
    public ResourceDto reviewResource(Long id, AuditRequest req, Long reviewerId) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
        applyReview(r.getIsApproved(), r.getRejectionReason(), req, reviewerId,
                () -> r.setIsApproved(true),
                () -> r.setIsApproved(false),
                r::setRejectionReason,
                () -> r.setApprovedBy(reviewerId),
                () -> r.setApprovedDate(LocalDateTime.now()));
        return ResourceDto.from(r);
    }

    @Transactional(readOnly = true)
    public List<ModelDto> pendingModels() {
        return modelRepository.findByIsApprovedFalseAndRejectionReasonIsNullOrderByCreateDateDesc()
                .stream().map(ModelDto::from).toList();
    }

    @Transactional
    public ModelDto reviewModel(Long id, AuditRequest req, Long reviewerId) {
        Model3D m = modelRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("模型不存在"));
        applyReview(m.getIsApproved(), m.getRejectionReason(), req, reviewerId,
                () -> m.setIsApproved(true),
                () -> m.setIsApproved(false),
                m::setRejectionReason,
                () -> m.setApprovedBy(reviewerId),
                () -> m.setApprovedDate(LocalDateTime.now()));
        return ModelDto.from(m);
    }

    private void applyReview(Boolean currentApproved, String currentReason, AuditRequest req, Long reviewerId,
                             Runnable approve, Runnable reject,
                             java.util.function.Consumer<String> setReason,
                             Runnable setReviewer, Runnable setDate) {
        if (Boolean.TRUE.equals(req.approved())) {
            if (Boolean.TRUE.equals(currentApproved)) {
                throw ApiException.badRequest("该内容已审核通过");
            }
            approve.run();
            setReason.accept(null);
        } else {
            setReason.accept(req.reason() == null || req.reason().isBlank() ? "不符合平台规范" : req.reason());
            reject.run();
        }
        setReviewer.run();
        setDate.run();
    }
}
