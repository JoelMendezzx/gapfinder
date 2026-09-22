package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GapBasicDTO {
	private Long id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int durationMinutes;
}
