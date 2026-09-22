package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OpenTableCompleteDTO extends OpenTableBasicDTO {
	private ActivityBasicDTO activity;
	private UserBasicDTO creator;
	private BuildingBasicDTO building;
	private List<MessageBasicDTO> messages;
	private List<OpenTableParticipantBasicDTO> participants;
}
