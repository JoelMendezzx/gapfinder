package com.backend.gapfinder.entities.classblock;

import com.backend.gapfinder.enums.DayOfWeekEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ClassBlockRepository extends JpaRepository<ClassBlockEntity, Long> {

    List<ClassBlockEntity> findByUserId(Long userId);

    boolean existsByUserIdAndSubjectAndDayOfWeekAndStartTimeAndEndTime(
            Long userId,
            String subject,
            DayOfWeekEnum dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );
}