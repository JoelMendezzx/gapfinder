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

	public static ClassBlockBasicDTO fromEntity(ClassBlockEntity entity) {
		ClassBlockBasicDTO dto = new ClassBlockBasicDTO();
		dto.setId(entity.getId());
		dto.setSubject(entity.getSubject());
		dto.setLocation(entity.getLocation());
		dto.setDayOfWeek(entity.getDayOfWeek());
		dto.setStartTime(entity.getStartTime());
		dto.setEndTime(entity.getEndTime());
		return dto;
	}
}
