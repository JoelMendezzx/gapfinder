package com.backend.gapfinder.repository;

import com.backend.gapfinder.enums.DayOfWeekEnum;
import com.backend.gapfinder.model.ClassBlockModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ClassBlockRepository extends JpaRepository<ClassBlockModel, Long> {

    List<ClassBlockModel> findByUserId(Long userId);

    boolean existsByUserIdAndSubjectAndDayOfWeekAndStartTimeAndEndTime(
            Long userId,
            String subject,
            DayOfWeekEnum dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );
}