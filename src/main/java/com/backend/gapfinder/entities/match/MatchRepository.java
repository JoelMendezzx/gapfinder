package com.backend.gapfinder.entities.match;

import com.backend.gapfinder.enums.MatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    // Historial completo de matches de un usuario (como requester o receiver), más recientes primero
    @Query("""
        SELECT m FROM MatchEntity m
        WHERE m.requester.id = :userId OR m.receiver.id = :userId
        ORDER BY m.createdAt DESC
    """)
    List<MatchEntity> findHistoryByUser(@Param("userId") Long userId);

    // Buscar si ya existe un match activo (PENDING o ACCEPTED) entre dos usuarios, en cualquier dirección
    @Query("""
        SELECT m FROM MatchEntity m
        WHERE m.status IN (:statuses)
          AND ((m.requester.id = :userAId AND m.receiver.id = :userBId)
            OR (m.requester.id = :userBId AND m.receiver.id = :userAId))
    """)
    Optional<MatchEntity> findActiveBetweenUsers(
        @Param("userAId") Long userAId,
        @Param("userBId") Long userBId,
        @Param("statuses") List<MatchStatusEnum> statuses
    );

    // ids de usuarios con los que :userId ya tiene un match PENDING/ACCEPTED
    // (para excluirlos de la lista de candidatos)
    @Query("""
        SELECT CASE WHEN m.requester.id = :userId THEN m.receiver.id ELSE m.requester.id END
        FROM MatchEntity m
        WHERE m.status IN (:statuses)
          AND (m.requester.id = :userId OR m.receiver.id = :userId)
    """)
    List<Long> findActivePartnerIds(
        @Param("userId") Long userId,
        @Param("statuses") List<MatchStatusEnum> statuses
    );

    // BQ 7: cuantos matches (invitacion directa) hay en cada estado
    @Query("""
        SELECT m.status, COUNT(m.id)
        FROM MatchEntity m
        GROUP BY m.status
    """)
    List<Object[]> countByStatus();
}
