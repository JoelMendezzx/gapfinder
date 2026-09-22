package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.OpenTableAbandonmentModel;
// package: ponlo junto a tus otros repositories

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OpenTableAbandonmentRepository extends JpaRepository<OpenTableAbandonmentModel, Long> {

    // Cada fila es [step, cantidad de abandonos en ese paso] desde la fecha dada
    @Query("select a.step, count(a) from OpenTableAbandonmentModel a "
         + "where a.abandonedAt >= :since group by a.step")
    List<Object[]> countGroupedByStep(@Param("since") LocalDateTime since);
}