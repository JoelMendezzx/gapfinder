package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.GapModel;
import com.backend.gapfinder.model.UserModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GapRepository extends JpaRepository<GapModel, Long> {

    // 1. Obtener todos los GAPs de un usuario
    List<GapModel> findByUserId(Long userId);

    // 2. Obtener los GAPs de un usuario cuyo startTime
    // esté dentro de un rango de fechas
    List<GapModel> findByUserIdAndStartTimeBetween(
        Long userId,
        LocalDateTime start,
        LocalDateTime end
    );

    // 3. Buscar el primer GAP que contenga un intervalo determinado
    Optional<GapModel> findFirstByUserIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeAsc(
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    // 4. Obtener amigos que están libres en un momento determinado
    @Query("""
        SELECT DISTINCT g.user
        FROM GapModel g
        WHERE g.user.id IN :friendIds
          AND :now BETWEEN g.startTime AND g.endTime
    """)
    List<UserModel> findAvailableFriends(
        @Param("friendIds") List<Long> friendIds,
        @Param("now") LocalDateTime now
    );


    // 5. Todos los GAPs activos ahora mismo, de cualquier usuario excepto el mismo
    @Query("""
        SELECT g FROM GapModel g
        WHERE g.user.id <> :excludeUserId
          AND :now BETWEEN g.startTime AND g.endTime
    """)
    List<GapModel> findActiveGapsExcludingUser(
        @Param("excludeUserId") Long excludeUserId,
        @Param("now") LocalDateTime now
    );

    List<GapModel> findByStartTimeBetweenAndStartingSoonNotifiedFalse(LocalDateTime from, LocalDateTime to);

    List<GapModel> findByStartTimeLessThanEqualAndStartedNotifiedFalse(LocalDateTime now);

    List<GapModel> findByEndTimeBetweenAndEndingSoonNotifiedFalse(LocalDateTime from, LocalDateTime to);

    List<GapModel> findByEndTimeLessThanEqualAndEndedNotifiedFalse(LocalDateTime now);

    List<GapModel> findByUserIdAndEndTimeLessThanEqualOrderByStartTimeDesc(Long userId, LocalDateTime now);
}

