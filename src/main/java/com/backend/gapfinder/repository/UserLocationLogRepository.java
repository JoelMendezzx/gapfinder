package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.UserLocationLogModel;
import com.backend.gapfinder.repository.projection.BuildingFreeTimeProjection;
import com.backend.gapfinder.repository.projection.BuildingTimeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserLocationLogRepository extends JpaRepository<UserLocationLogModel, Long> {

    List<UserLocationLogModel> findByGapIdOrderByRecordedAtAsc(Long gapId);

    List<UserLocationLogModel> findByUserIdOrderByRecordedAtAsc(Long userId);

    // Calcula, para todos los gaps de un usuario, el building donde acumuló más tiempo
    @Query(value = """
        WITH tramos AS (
            SELECT 
                l.building_id,
                l.recorded_at,
                LEAD(l.recorded_at) OVER (PARTITION BY l.gap_id ORDER BY l.recorded_at) AS siguiente,
                g.end_time AS gap_end_time
            FROM user_location_logs l
            JOIN gaps g ON g.id = l.gap_id
            WHERE g.user_id = :userId
        )
        SELECT building_id AS buildingId,
               SUM(EXTRACT(EPOCH FROM (COALESCE(siguiente, gap_end_time) - recorded_at)) / 60) AS minutos
        FROM tramos
        WHERE building_id IS NOT NULL
        GROUP BY building_id
        ORDER BY minutos DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<BuildingTimeProjection> findFavoriteBuildingForUser(@Param("userId") Long userId);

    // Calcula, para todos los usuarios, los minutos acumulados en GAPs y los estudiantes distintos por building desde una fecha
    @Query(value = """
        WITH tramos AS (
            SELECT 
                l.building_id,
                l.user_id,
                l.recorded_at,
                LEAD(l.recorded_at) OVER (PARTITION BY l.gap_id ORDER BY l.recorded_at) AS siguiente,
                g.end_time AS gap_end_time
            FROM user_location_logs l
            JOIN gaps g ON g.id = l.gap_id
            WHERE l.recorded_at >= :since
        )
        SELECT building_id AS buildingId,
               CAST(ROUND(SUM(EXTRACT(EPOCH FROM (COALESCE(siguiente, gap_end_time) - recorded_at)) / 60)) AS BIGINT) AS totalMinutes,
               COUNT(DISTINCT user_id) AS students
        FROM tramos
        WHERE building_id IS NOT NULL
        GROUP BY building_id
        """, nativeQuery = true)
    List<BuildingFreeTimeProjection> findFreeTimePerBuilding(@Param("since") LocalDateTime since);
}