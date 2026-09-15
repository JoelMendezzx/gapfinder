package com.backend.gapfinder.entities.opentableparticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpenTableParticipantRepository
        extends JpaRepository<OpenTableParticipantEntity, Long> {

    Optional<OpenTableParticipantEntity>
    findByOpenTableIdAndUserId(
            Long openTableId,
            Long userId
    );

    List<OpenTableParticipantEntity>
    findByOpenTableId(Long openTableId);
}