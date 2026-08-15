package com.learnai.service;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.MyLearningDto;
import com.learnai.dto.learning.ResourceCreateRequest;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.entity.Download;
import com.learnai.entity.LearningRecord;
import com.learnai.entity.LearningResource;
import com.learnai.entity.ResourceCategory;
import com.learnai.exception.ApiException;
import com.learnai.repository.DownloadRepository;
import com.learnai.repository.LearningRecordRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.ResourceCategoryRepository;
import com.learnai.security.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学习资源：公开列表/详情（浏览量）、提交（待审核）、点赞、下载、我的学习
 */
@Service
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository resourceRepository;
    private final LearningRecordRepository recordRepository;
    private final DownloadRepository downloadRepository;
    private final ResourceCategoryRepository categoryRepository;
    private final FileStorageService fileStorage;

    /** 公开列表：只显示已审核且公开的资源 */
    @Transactional(readOnly = true)
    public PageResponse<ResourceDto> list(Long categoryId, String search, int page, int size, String sort) {
        Specification<LearningResource> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.isTrue(root.get("isApproved")));
            ps.add(cb.isTrue(root.get("isPublic")));
            if (categoryId != null) {
                // 选中父分类时自动包含其全部子分类的资源
                List<Long> ids = new ArrayList<>();
                ids.add(categoryId);
                ids.addAll(categoryRepository
                        .findByIsActiveTrueAndParentCategoryIdOrderBySortOrderAsc(categoryId)
                        .stream().map(ResourceCategory::getCategoryId).toList());
                ps.add(root.get("category").get("categoryId").in(ids));
            }
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.add(cb.or(
                        cb.like(root.get("resourceTitle"), like),
                        cb.like(root.get("description"), like),
                        cb.like(root.get("author"), like)));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        PageRequest pageable = PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50), sortFor(sort));
        Page<LearningResource> result = resourceRepository.findAll(spec, pageable);
        return PageResponse.of(result.map(ResourceDto::from));
    }

    /** 详情：未审核/未公开的资源仅管理员和审核员可见；浏览量 +1 */
    @Transactional
    public ResourceDto detail(Long id) {
        LearningResource r = findForRead(id);
        r.setViewCount(r.getViewCount() + 1);
        return ResourceDto.from(r);
    }

    /** 提交学习资源：进入待审核状态 */
    @Transactional
    public ResourceDto create(ResourceCreateRequest req, MultipartFile file) {
        ResourceCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> ApiException.badRequest("所选分类不存在"));
        String stored = fileStorage.storeResourceFile(file);

        LearningResource r = new LearningResource();
        r.setResourceTitle(req.getTitle());
        r.setDescription(req.getDescription());
        r.setCategory(category);
        r.setPrice(req.getPrice() == null ? BigDecimal.ZERO : req.getPrice());
        r.setIsFree(req.getIsFree() == null ? Boolean.TRUE : req.getIsFree());
        r.setDifficultyLevel(req.getDifficultyLevel());
        r.setDurationMinutes(req.getDurationMinutes());
        r.setLearningType(req.getLearningType());
        r.setVideoUrl(req.getVideoUrl());
        r.setPreviewUrl(req.getPreviewUrl());
        r.setIsPublic(req.getIsPublic() == null ? Boolean.TRUE : req.getIsPublic());
        r.setFilePath(stored);
        r.setOriginalFileName(file.getOriginalFilename());
        r.setIsApproved(false);
        r.setCreateDate(LocalDateTime.now());
        resourceRepository.save(r);
        return ResourceDto.from(r);
    }

    /** 点赞（演示简化：计数 +1） */
    @Transactional
    public ResourceDto like(Long id) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
        r.setLikeCount(r.getLikeCount() + 1);
        return ResourceDto.from(r);
    }

    /** 下载：记录下载历史并返回文件 */
    @Transactional
    public StoredFile getDownloadFile(Long userId, Long resourceId, String ip) {
        LearningResource r = findForRead(resourceId);
        if (r.getFilePath() == null || r.getFilePath().isBlank()) {
            throw ApiException.badRequest("该资源暂无可下载文件");
        }
        Download d = new Download();
        d.setUserId(userId);
        d.setResourceId(resourceId);
        d.setIpAddress(ip);
        downloadRepository.save(d);
        return new StoredFile(fileStorage.load(r.getFilePath()),
                r.getOriginalFileName() != null && !r.getOriginalFileName().isBlank()
                        ? r.getOriginalFileName() : "download");
    }

    /** 我的学习列表 */
    @Transactional(readOnly = true)
    public List<MyLearningDto> myLearning(Long userId) {
        List<LearningRecord> records = recordRepository.findByUserIdOrderByStartTimeDesc(userId);
        List<Long> ids = records.stream().map(LearningRecord::getResourceId).toList();
        Map<Long, LearningResource> resources = ids.isEmpty() ? Map.of()
                : resourceRepository.findAllById(ids).stream()
                        .collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
        return records.stream().map(rec -> {
            LearningResource r = resources.get(rec.getResourceId());
            if (r == null) {
                return null;
            }
            return new MyLearningDto(
                    r.getResourceId(),
                    r.getResourceTitle(),
                    r.getCategory() == null ? null : r.getCategory().getCategoryName(),
                    r.getThumbnailUrl(),
                    rec.getStatus() == null ? null : rec.getStatus().name(),
                    rec.getProgress(),
                    rec.getStartTime(),
                    rec.getEndTime(),
                    rec.getDurationMinutes(),
                    rec.getScore(),
                    r.getIsFree(),
                    r.getDifficultyLevel());
        }).filter(Objects::nonNull).toList();
    }

    /** 读取可见性校验：未审核/未公开的资源仅管理员和审核员可见 */
    private LearningResource findForRead(Long id) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("资源不存在"));
        boolean visible = Boolean.TRUE.equals(r.getIsApproved()) && Boolean.TRUE.equals(r.getIsPublic());
        if (!visible && !SecurityUtils.isAdmin() && !SecurityUtils.isAuditor()) {
            throw ApiException.notFound("资源不存在");
        }
        return r;
    }

    private Sort sortFor(String sort) {
        return switch (sort == null ? "" : sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "viewCount"));
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            default -> Sort.by(Sort.Direction.DESC, "createDate");
        };
    }

    /** 下载文件载体 */
    public record StoredFile(Resource resource, String originalName) {
    }
}
