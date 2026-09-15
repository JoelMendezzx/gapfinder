package com.backend.gapfinder.entities.userlocationlog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLocationLogRepository extends JpaRepository<UserLocationLogEntity, Long> {

    List<UserLocationLogEntity> findByGapIdOrderByRecordedAtAsc(Long gapId);

    List<UserLocationLogEntity> findByUserIdOrderByRecordedAtAsc(Long userId);
}