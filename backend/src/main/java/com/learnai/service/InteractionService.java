package com.learnai.service;

import com.learnai.dto.interaction.CommentCreateRequest;
import com.learnai.dto.interaction.CommentDto;
import com.learnai.dto.interaction.DownloadItemDto;
import com.learnai.dto.interaction.FavoriteItemDto;
import com.learnai.entity.Comment;
import com.learnai.entity.Download;
import com.learnai.entity.Favorite;
import com.learnai.entity.LearningResource;
import com.learnai.entity.Model3D;
import com.learnai.entity.User;
import com.learnai.exception.ApiException;
import com.learnai.repository.CommentRepository;
import com.learnai.repository.DownloadRepository;
import com.learnai.repository.FavoriteRepository;
import com.learnai.repository.LearningResourceRepository;
import com.learnai.repository.Model3DRepository;
import com.learnai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 互动：收藏（双类型切换）、评论（树形）、下载历史
 */
@Service
@RequiredArgsConstructor
public class InteractionService {

    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final DownloadRepository downloadRepository;
    private final LearningResourceRepository resourceRepository;
    private final Model3DRepository modelRepository;
    private final UserRepository userRepository;

    /** 收藏/取消收藏（幂等切换），返回当前是否已收藏 */
    @Transactional
    public boolean toggleFavorite(Long userId, Long resourceId, Long modelId) {
        if ((resourceId == null) == (modelId == null)) {
            throw ApiException.badRequest("resourceId 与 modelId 必须二选一");
        }
        if (resourceId != null) {
            resourceRepository.findById(resourceId)
                    .orElseThrow(() -> ApiException.notFound("资源不存在"));
            var existing = favoriteRepository.findByUserIdAndResourceId(userId, resourceId);
            if (existing.isPresent()) {
                favoriteRepository.delete(existing.get());
                return false;
            }
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setResourceId(resourceId);
            favoriteRepository.save(f);
            return true;
        }
        modelRepository.findById(modelId)
                .orElseThrow(() -> ApiException.notFound("模型不存在"));
        var existing = favoriteRepository.findByUserIdAndModelId(userId, modelId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false;
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setModelId(modelId);
        favoriteRepository.save(f);
        return true;
    }

    /** 我的收藏（资源 + 模型合并，按时间倒序） */
    @Transactional(readOnly = true)
    public List<FavoriteItemDto> myFavorites(Long userId) {
        List<Favorite> resourceFavs = favoriteRepository.findByUserIdAndResourceIdIsNotNullOrderByAddedDateDesc(userId);
        List<Favorite> modelFavs = favoriteRepository.findByUserIdAndModelIdIsNotNullOrderByAddedDateDesc(userId);

        Map<Long, LearningResource> resources = resourceFavs.isEmpty() ? Map.of()
                : resourceRepository.findAllById(resourceFavs.stream().map(Favorite::getResourceId).toList())
                        .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
        Map<Long, Model3D> models = modelFavs.isEmpty() ? Map.of()
                : modelRepository.findAllById(modelFavs.stream().map(Favorite::getModelId).toList())
                        .stream().collect(Collectors.toMap(Model3D::getModelId, x -> x));

        List<FavoriteItemDto> result = new ArrayList<>();
        for (Favorite f : resourceFavs) {
            LearningResource r = resources.get(f.getResourceId());
            if (r == null) {
                continue;
            }
            result.add(new FavoriteItemDto(f.getFavoriteId(), "resource", r.getResourceId(),
                    r.getResourceTitle(), r.getThumbnailUrl() != null ? r.getThumbnailUrl() : r.getPreviewUrl(),
                    r.getPrice(), f.getAddedDate()));
        }
        for (Favorite f : modelFavs) {
            Model3D m = models.get(f.getModelId());
            if (m == null) {
                continue;
            }
            result.add(new FavoriteItemDto(f.getFavoriteId(), "model", m.getModelId(),
                    m.getModelName(), m.getPreviewUrl(), m.getPrice(), f.getAddedDate()));
        }
        result.sort(Comparator.comparing(FavoriteItemDto::addedDate).reversed());
        return result;
    }

    /** 发表评论（自动过审） */
    @Transactional
    public CommentDto addComment(Long userId, CommentCreateRequest req) {
        if ((req.resourceId() == null) == (req.modelId() == null)) {
            throw ApiException.badRequest("resourceId 与 modelId 必须二选一");
        }
        if (req.resourceId() != null) {
            resourceRepository.findById(req.resourceId())
                    .orElseThrow(() -> ApiException.notFound("资源不存在"));
        } else {
            modelRepository.findById(req.modelId())
                    .orElseThrow(() -> ApiException.notFound("模型不存在"));
        }
        if (req.parentCommentId() != null) {
            commentRepository.findById(req.parentCommentId())
                    .orElseThrow(() -> ApiException.badRequest("回复的评论不存在"));
        }
        Comment c = new Comment();
        c.setUserId(userId);
        c.setResourceId(req.resourceId());
        c.setModelId(req.modelId());
        c.setParentCommentId(req.parentCommentId());
        c.setContent(req.content());
        c.setIsApproved(true);
        commentRepository.save(c);
        return toCommentDto(c, userRepository.findById(userId).map(User::getUsername).orElse("用户"));
    }

    /** 评论树（仅已过审） */
    @Transactional(readOnly = true)
    public List<CommentDto> listComments(Long resourceId, Long modelId) {
        if ((resourceId == null) == (modelId == null)) {
            throw ApiException.badRequest("resourceId 与 modelId 必须二选一");
        }
        List<Comment> flat = resourceId != null
                ? commentRepository.findByResourceIdAndIsApprovedTrueOrderByCommentDateAsc(resourceId)
                : commentRepository.findByModelIdAndIsApprovedTrueOrderByCommentDateAsc(modelId);
        if (flat.isEmpty()) {
            return List.of();
        }
        Map<Long, String> usernames = userRepository.findAllById(
                        flat.stream().map(Comment::getUserId).distinct().toList())
                .stream().collect(Collectors.toMap(User::getUserId, User::getUsername));

        Map<Long, CommentDto> byId = new HashMap<>();
        for (Comment c : flat) {
            byId.put(c.getCommentId(), toCommentDto(c, usernames.getOrDefault(c.getUserId(), "用户")));
        }
        List<CommentDto> roots = new ArrayList<>();
        for (CommentDto dto : byId.values()) {
            if (dto.parentId() != null && byId.containsKey(dto.parentId())) {
                byId.get(dto.parentId()).replies().add(dto);
            } else {
                roots.add(dto);
            }
        }
        return roots;
    }

    /** 下载历史（资源 + 模型） */
    @Transactional(readOnly = true)
    public List<DownloadItemDto> myDownloads(Long userId) {
        List<Download> downloads = downloadRepository.findByUserIdOrderByDownloadTimeDesc(userId);
        Map<Long, LearningResource> resources = resourceRepository.findAllById(
                        downloads.stream().map(Download::getResourceId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(LearningResource::getResourceId, x -> x));
        Map<Long, Model3D> models = modelRepository.findAllById(
                        downloads.stream().map(Download::getModelId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(Model3D::getModelId, x -> x));
        return downloads.stream().map(d -> {
            if (d.getResourceId() != null) {
                LearningResource r = resources.get(d.getResourceId());
                if (r == null) {
                    return null;
                }
                return new DownloadItemDto(d.getDownloadId(), "resource", r.getResourceId(),
                        r.getResourceTitle(), d.getDownloadTime());
            }
            Model3D m = models.get(d.getModelId());
            if (m == null) {
                return null;
            }
            return new DownloadItemDto(d.getDownloadId(), "model", m.getModelId(),
                    m.getModelName(), d.getDownloadTime());
        }).filter(Objects::nonNull).toList();
    }

    private CommentDto toCommentDto(Comment c, String username) {
        return new CommentDto(c.getCommentId(), c.getParentCommentId(), c.getUserId(),
                username, c.getContent(), c.getCommentDate(), new ArrayList<>());
    }
}
