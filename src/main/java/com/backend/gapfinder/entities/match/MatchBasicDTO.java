package com.backend.gapfinder.entities.match;

import com.backend.gapfinder.enums.MatchStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchBasicDTO {
	private Long id;
	private MatchStatusEnum status;
	private LocalDateTime overlapStart;
	private LocalDateTime overlapEnd;
	private LocalDateTime createdAt;
}