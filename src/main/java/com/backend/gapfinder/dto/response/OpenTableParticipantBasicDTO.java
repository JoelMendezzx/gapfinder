package com.backend.gapfinder.dto.response;

import com.backend.gapfinder.enums.ResponseStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpenTableParticipantBasicDTO {
	private Long id;
	private ResponseStatusEnum rsvp;
	private LocalDateTime respondedAt;
	private Boolean enjoyed;
	private UserBasicDTO user;
}
