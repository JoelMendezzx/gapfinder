package com.backend.gapfinder.entities.rating;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    List<RatingEntity> findByRatedUserId(Long ratedUserId);

    boolean existsByMatchIdAndRaterId(Long matchId, Long raterId);

    boolean existsByOpenTableIdAndRaterId(Long openTableId, Long raterId);

}