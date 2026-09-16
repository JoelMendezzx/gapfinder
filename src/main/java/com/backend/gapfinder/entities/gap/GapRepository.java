package com.backend.gapfinder.entities.gap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchStatusEnum;

@Repository
public interface GapRepository extends JpaRepository<GapEntity, Long> {

    // 1. Obtener todos los GAPs de un usuario
    List<GapEntity> findByUserId(Long userId);

    // 2. Obtener los GAPs de un usuario cuyo startTime
    // esté dentro de un rango de fechas
    List<GapEntity> findByUserIdAndStartTimeBetween(
        Long userId,
        LocalDateTime start,
        LocalDateTime end
    );

    // 3. Buscar el primer GAP que contenga un intervalo determinado
    Optional<GapEntity> findFirstByUserIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeAsc(
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    // 4. Obtener amigos que están libres en un momento determinado
    @Query("""
        SELECT DISTINCT g.user
        FROM GapEntity g
        WHERE g.user.id IN :friendIds
          AND :now BETWEEN g.startTime AND g.endTime
    """)
    List<UserEntity> findAvailableFriends(
        @Param("friendIds") List<Long> friendIds,
        @Param("now") LocalDateTime now
    );


    // 5. Todos los GAPs activos ahora mismo, de cualquier usuario excepto el mismo
    @Query("""
        SELECT g FROM GapEntity g
        WHERE g.user.id <> :excludeUserId
          AND :now BETWEEN g.startTime AND g.endTime
    """)
    List<GapEntity> findActiveGapsExcludingUser(
        @Param("excludeUserId") Long excludeUserId,
        @Param("now") LocalDateTime now
    );

    // ---- Consultas para las Business Questions ----

    // BQ 5: cuantos GAPs se publicaron de cada duracion
    @Query("""
        SELECT g.durationMinutes, COUNT(g.id)
        FROM GapEntity g
        GROUP BY g.durationMinutes
        ORDER BY g.durationMinutes
    """)
    List<Object[]> countGapsByDuration();

    // BQ 5: de esos, cuantos si terminaron en un match aceptado
    @Query("""
        SELECT g.durationMinutes, COUNT(DISTINCT g.id)
        FROM GapEntity g, MatchEntity m
        WHERE (m.requesterGap.id = g.id OR m.receiverGap.id = g.id)
          AND m.status = :status
        GROUP BY g.durationMinutes
        ORDER BY g.durationMinutes
    """)
    List<Object[]> countMatchedGapsByDuration(@Param("status") MatchStatusEnum status);

    // BQ 9: cuantos GAPs se publicaron con cada opcion de visibilidad
    @Query("""
        SELECT g.visibilityScope, COUNT(g.id)
        FROM GapEntity g
        WHERE g.visibilityScope IS NOT NULL
        GROUP BY g.visibilityScope
    """)
    List<Object[]> countGapsByVisibility();

    // BQ 9: cuantas conexiones aceptadas salieron de cada opcion de visibilidad
    @Query("""
        SELECT g.visibilityScope, COUNT(DISTINCT m.id)
        FROM GapEntity g, MatchEntity m
        WHERE (m.requesterGap.id = g.id OR m.receiverGap.id = g.id)
          AND m.status = :status
          AND g.visibilityScope IS NOT NULL
        GROUP BY g.visibilityScope
    """)
    List<Object[]> countAcceptedByVisibility(@Param("status") MatchStatusEnum status);
}
