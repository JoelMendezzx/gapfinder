package com.backend.gapfinder.dto.response;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpenTableBasicDTO {
	private Long id;
	private String description;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private OpenTableStatusEnum status;
	private LocalDateTime createdAt;
}
