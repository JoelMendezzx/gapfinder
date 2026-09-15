package com.backend.gapfinder.entities.classblock;

import com.backend.gapfinder.enums.DayOfWeekEnum;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ClassBlockBasicDTO {
	private Long id;
	private String subject;
	private String location;
	private DayOfWeekEnum dayOfWeek;
	private LocalTime startTime;
	private LocalTime endTime;
}
