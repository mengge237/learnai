package com.learnai.service;

import com.learnai.dto.common.PageResponse;
import com.learnai.dto.learning.MyPathDto;
import com.learnai.dto.learning.PathCreateRequest;
import com.learnai.dto.learning.PathDetailDto;
import com.learnai.dto.learning.PathDto;
import com.learnai.dto.learning.ResourceDto;
import com.learnai.entity.LearningPath;
import com.learnai.entity.LearningResource;
import com.learnai.entity.PathResource;
import com.learnai.entity.UserLearningPath;
import com.learnai.exception.ApiException;
import com.learnai.repository.LearningPathRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.PathResourceRepository;
import com.learnai.repository.UserLearningPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学习路径：公开列表/详情、报名（幂等）、我的路径、管理员 CRUD 与资源序列管理
 */
@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository pathRepository;
    private final PathResourceRepository pathResourceRepository;
    private final UserLearningPathRepository userPathRepository;
    private final LearningResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public PageResponse<PathDto> list(int page, int size) {
        Page<LearningPath> result = pathRepository.findByIsActiveTrue(
                PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, size), 50)));
        return PageResponse.of(result.map(p -> PathDto.from(p, (int) pathResourceRepository.countByPathId(p.getPathId()))));
    }

    @Transactional
    public PathDetailDto detail(Long id) {
        return detailInternal(id, true);
    }

    /** 报名（幂等：重复报名返回已有记录），报名数 +1 */
    @Transactional
    public MyPathDto enroll(Long userId, Long pathId) {
        LearningPath p = pathRepository.findById(pathId)
                .orElseThrow(() -> ApiException.notFound("学习路径不存在"));
        UserLearningPath ulp = userPathRepository.findByUserIdAndPathId(userId, pathId)
                .orElseGet(() -> {
                    UserLearningPath n = new UserLearningPath();
                    n.setUserId(userId);
                    n.setPathId(pathId);
                    p.setEnrollmentCount(p.getEnrollmentCount() + 1);
                    return userPathRepository.save(n);
                });
        return toMyDto(ulp, p);
    }

    /** 我报名的路径 */
    @Transactional(readOnly = true)
    public List<MyPathDto> myPaths(Long userId) {
        List<UserLearningPath> list = userPathRepository.findByUserIdOrderByEnrollDateDesc(userId);
        List<Long> ids = list.stream().map(UserLearningPath::getPathId).toList();
        Map<Long, LearningPath> paths = ids.isEmpty() ? Map.of()
                : pathRepository.findAllById(ids).stream()
                        .collect(Collectors.toMap(LearningPath::getPathId, x -> x));
        return list.stream()
                .map(ulp -> toMyDto(ulp, paths.get(ulp.getPathId())))
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public PathDto create(PathCreateRequest req) {
        LearningPath p = new LearningPath();
        apply(p, req);
        pathRepository.save(p);
        return PathDto.from(p, 0);
    }

    @Transactional
    public PathDto update(Long id, PathCreateRequest req) {
        LearningPath p = pathRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("学习路径不存在"));
        apply(p, req);
        return PathDto.from(p, (int) pathResourceRepository.countByPathId(id));
    }

    @Transactional
    public void delete(Long id) {
        pathRepository.deleteById(id);
        pathResourceRepository.deleteByPathId(id);
        userPathRepository.deleteByPathId(id);
    }

    /** 整表替换路径资源序列 */
    @Transactional
    public PathDetailDto updateResources(Long id, List<Long> resourceIds) {
        pathRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("学习路径不存在"));
        if (!resourceIds.isEmpty()) {
            List<LearningResource> resources = resourceRepository.findAllById(resourceIds);
            if (resources.size() != resourceIds.size()) {
                throw ApiException.badRequest("资源列表中包含不存在的资源");
            }
        }
        pathResourceRepository.deleteByPathId(id);
        int seq = 1;
        for (Long rid : resourceIds) {
            PathResource pr = new PathResource();
            pr.setPathId(id);
            pr.setResourceId(rid);
            pr.setSequenceNumber(seq++);
            pathResourceRepository.save(pr);
        }
        return detailInternal(id, false);
    }

    private PathDetailDto detailInternal(Long id, boolean countView) {
        LearningPath p = pathRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("学习路径不存在"));
        if (countView) {
            p.setViewCount(p.getViewCount() + 1);
        }
        List<PathResource> links = pathResourceRepository.findByPathIdOrderBySequenceNumberAsc(id);
        Map<Long, LearningResource> resources = links.isEmpty() ? Map.of()
                : resourceRepository.findAllById(links.stream().map(PathResource::getResourceId).toList())
                        .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
        List<ResourceDto> list = links.stream()
                .map(l -> resources.get(l.getResourceId()))
                .filter(Objects::nonNull)
                .map(ResourceDto::from)
                .toList();
        return new PathDetailDto(
                p.getPathId(), p.getPathName(), p.getDescription(), p.getTargetAudience(),
                p.getEstimatedHours(), p.getDifficultyLevel(), p.getViewCount(), p.getEnrollmentCount(),
                p.getCreateDate(), p.getIsActive(), p.getCoverImageUrl(), list);
    }

    private void apply(LearningPath p, PathCreateRequest req) {
        p.setPathName(req.getName());
        p.setDescription(req.getDescription());
        p.setTargetAudience(req.getTargetAudience());
        p.setEstimatedHours(req.getEstimatedHours());
        p.setDifficultyLevel(req.getDifficultyLevel() == null ? 1 : req.getDifficultyLevel());
        p.setCoverImageUrl(req.getCoverImageUrl());
        p.setIsActive(req.getIsActive() == null ? Boolean.TRUE : req.getIsActive());
    }

    private MyPathDto toMyDto(UserLearningPath ulp, LearningPath p) {
        if (p == null) {
            return null;
        }
        return new MyPathDto(
                ulp.getUserPathId(),
                p.getPathId(),
                p.getPathName(),
                p.getCoverImageUrl(),
                ulp.getStatus() == null ? null : ulp.getStatus().name(),
                ulp.getProgress(),
                ulp.getEnrollDate(),
                ulp.getCompletedDate(),
                p.getEstimatedHours());
    }
}
