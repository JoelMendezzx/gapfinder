package com.backend.gapfinder.entities.gap;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GapBasicDTO {
	private Long id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int durationMinutes;
}
