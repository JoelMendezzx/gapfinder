package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.OpenTableParticipantModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpenTableParticipantRepository
        extends JpaRepository<OpenTableParticipantModel, Long> {

    Optional<OpenTableParticipantModel>
    findByOpenTableIdAndUserId(
            Long openTableId,
            Long userId
    );

        @Query("""
                SELECT participant
                FROM OpenTableParticipantModel participant
                JOIN FETCH participant.user
                WHERE participant.openTable.id = :openTableId
                """)
        List<OpenTableParticipantModel> findByOpenTableId(@Param("openTableId") Long openTableId);

        List<OpenTableParticipantModel>
        findByUserIdOrderByOpenTableStartTimeDesc(Long userId);
}