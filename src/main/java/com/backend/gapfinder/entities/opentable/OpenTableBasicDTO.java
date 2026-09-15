package com.backend.gapfinder.entities.opentable;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpenTableBasicDTO {
	private Long id;
	private String description;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private boolean privateTable;
	private OpenTableStatusEnum status;
	private LocalDateTime createdAt;
}
