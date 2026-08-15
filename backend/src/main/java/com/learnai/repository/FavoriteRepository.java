package com.learnai.repository;

import com.learnai.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndResourceId(Long userId, Long resourceId);

    Optional<Favorite> findByUserIdAndModelId(Long userId, Long modelId);

    List<Favorite> findByUserIdAndResourceIdIsNotNullOrderByAddedDateDesc(Long userId);

    List<Favorite> findByUserIdAndModelIdIsNotNullOrderByAddedDateDesc(Long userId);
}
