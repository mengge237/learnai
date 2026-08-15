package com.learnai.service;

import com.learnai.dto.audit.AuditRecordDto;
import com.learnai.dto.audit.AuditRequest;
import com.learnai.dto.audit.AuditStatsDto;
import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.dto.market.ModelDto;
import com.learnai.entity.LearningResource;
import com.learnai.entity.Model3D;
import com.learnai.entity.User;
import com.learnai.exception.ApiException;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.Model3DRepository;
import com.learnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 审核：资源/模型的待审核列表、通过、驳回
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final LearningResourceRepository resourceRepository;
    private final Model3DRepository modelRepository;
    private final UserRepository userRepository;

    /** 审核工作台统计：待审数 + 累计/今日已审数 */
    @Transactional(readOnly = true)
    public AuditStatsDto stats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return new AuditStatsDto(
                resourceRepository.countByIsApprovedFalseAndRejectionReasonIsNull(),
                modelRepository.countByIsApprovedFalseAndRejectionReasonIsNull(),
                resourceRepository.countByApprovedDateIsNotNull() + modelRepository.countByApprovedDateIsNotNull(),
                resourceRepository.countByApprovedDateAfter(todayStart) + modelRepository.countByApprovedDateAfter(todayStart));
    }

    /** 资源审核历史（含审核人姓名） */
    @Transactional(readOnly = true)
    public PageResponse<AuditRecordDto> historyResources(int page, int size) {
        Page<LearningResource> result = resourceRepository.findByApprovedDateIsNotNullOrderByApprovedDateDesc(
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        Map<Long, String> reviewers = reviewerNames(result.getContent().stream()
                .map(LearningResource::getApprovedBy).filter(Objects::nonNull).toList());
        return PageResponse.of(result.map(r -> toRecord(
                "resource", r.getResourceId(), r.getResourceTitle(), r.getAuthor(),
                r.getIsApproved(), r.getRejectionReason(), r.getApprovedBy(), r.getApprovedDate(), reviewers)));
    }

    /** 模型审核历史（含审核人姓名） */
    @Transactional(readOnly = true)
    public PageResponse<AuditRecordDto> historyModels(int page, int size) {
        Page<Model3D> result = modelRepository.findByApprovedDateIsNotNullOrderByApprovedDateDesc(
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        Map<Long, String> reviewers = reviewerNames(result.getContent().stream()
                .map(Model3D::getApprovedBy).filter(Objects::nonNull).toList());
        return PageResponse.of(result.map(m -> toRecord(
                "model", m.getModelId(), m.getModelName(), m.getCreator(),
                m.getIsApproved(), m.getRejectionReason(), m.getApprovedBy(), m.getApprovedDate(), reviewers)));
    }

    private Map<Long, String> reviewerNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getUserId, User::getUsername));
    }

    private AuditRecordDto toRecord(String type, Long id, String title, String author, Boolean approved,
                                    String reason, Long reviewerId, LocalDateTime reviewedAt,
                                    Map<Long, String> reviewers) {
        return new AuditRecordDto(id, type, title, author,
                Boolean.TRUE.equals(approved) ? "approved" : "rejected",
                reason,
                reviewerId == null ? null : reviewers.getOrDefault(reviewerId, "未知"),
                reviewedAt);
    }

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
