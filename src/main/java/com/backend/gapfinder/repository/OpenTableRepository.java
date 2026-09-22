package com.backend.gapfinder.repository;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.model.OpenTableParticipantModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OpenTableRepository
        extends JpaRepository<OpenTableModel, Long> {

    List<OpenTableModel> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);

        @Query("""
                SELECT ot
                FROM OpenTableModel ot
                WHERE ot.status = :status
                    AND ot.endTime > :now
                    AND ot.creator.id <> :userId
                    AND NOT EXISTS (
                            SELECT participant.id
                            FROM OpenTableParticipantModel participant
                            WHERE participant.openTable = ot
                                AND participant.user.id = :userId
                                AND participant.rsvp = :rsvp
                    )
                ORDER BY ot.endTime ASC
                """)
        List<OpenTableModel> findDiscoverableForUser(
                        @Param("userId") Long userId,
                        @Param("status") OpenTableStatusEnum status,
                        @Param("rsvp") ResponseStatusEnum rsvp,
                        @Param("now") LocalDateTime now);

    List<OpenTableModel>
        findByBuildingIdAndStatusAndEndTimeAfterOrderByEndTimeDesc(
            Long buildingId,
            OpenTableStatusEnum status,
            LocalDateTime now
    );

    List<OpenTableModel>
        findByStatusAndEndTimeAfter(
            OpenTableStatusEnum status,
            LocalDateTime now
    );

    List<OpenTableModel>
    findByStatusAndEndTimeBefore(
            OpenTableStatusEnum status,
            LocalDateTime now
    );

    long countByCreatedAtGreaterThanEqual(LocalDateTime since);
}