package com.backend.gapfinder.entities.opentable;

import com.backend.gapfinder.enums.OpenTableStatusEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OpenTableRepository
        extends JpaRepository<OpenTableEntity, Long> {

    List<OpenTableEntity>
        findByBuildingIdAndPrivateTableFalseAndStatusAndEndTimeAfterOrderByEndTimeDesc(
            Long buildingId,
            OpenTableStatusEnum status,
            LocalDateTime now
    );

    List<OpenTableEntity>
        findByStatusAndPrivateTableFalseAndEndTimeAfter(
            OpenTableStatusEnum status,
            LocalDateTime now
    );

    List<OpenTableEntity>
    findByStatusAndEndTimeBefore(
            OpenTableStatusEnum status,
            LocalDateTime now
    );
}