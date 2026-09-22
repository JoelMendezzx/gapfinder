package com.backend.gapfinder.repository;

import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.model.MatchModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface MatchRepository extends JpaRepository<MatchModel, Long> {

    List<MatchModel> findByStatusInAndOverlapEndBefore(
        List<MatchStatusEnum> statuses, LocalDateTime dateTime);

    // Historial completo de matches de un usuario (como requester o receiver), más recientes primero
    @Query("""
        SELECT m FROM MatchModel m
        WHERE m.requester.id = :userId OR m.receiver.id = :userId
        ORDER BY m.createdAt DESC
    """)
    List<MatchModel> findHistoryByUser(@Param("userId") Long userId);

    // Buscar si ya existe un match activo entre dos usuarios, en cualquier dirección
    @Query("""
        SELECT m FROM MatchModel m
        WHERE m.status IN (:statuses)
          AND ((m.requester.id = :userAId AND m.receiver.id = :userBId)
            OR (m.requester.id = :userBId AND m.receiver.id = :userAId))
    """)
    Optional<MatchModel> findActiveBetweenUsers(
        @Param("userAId") Long userAId,
        @Param("userBId") Long userBId,
        @Param("statuses") List<MatchStatusEnum> statuses
    );

    // ids de usuarios con los que :userId ya tiene un match activo
    // (para excluirlos de la lista de candidatos)
    @Query("""
        SELECT CASE WHEN m.requester.id = :userId THEN m.receiver.id ELSE m.requester.id END
        FROM MatchModel m
        WHERE m.status IN (:statuses)
          AND (m.requester.id = :userId OR m.receiver.id = :userId)
    """)
    List<Long> findActivePartnerIds(
        @Param("userId") Long userId,
        @Param("statuses") List<MatchStatusEnum> statuses
    );

    @Query("""
        SELECT m FROM MatchModel m
        WHERE m.status IN (:statuses)
          AND (m.requester.id = :userId OR m.receiver.id = :userId)
          AND m.overlapStart < :dayEnd
          AND m.overlapEnd > :dayStart
    """)
    List<MatchModel> findActiveMatchesByUserAndDate(
        @Param("userId") Long userId,
        @Param("dayStart") java.time.LocalDateTime dayStart,
        @Param("dayEnd") java.time.LocalDateTime dayEnd,
        @Param("statuses") List<MatchStatusEnum> statuses
    );
}