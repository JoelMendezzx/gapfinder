package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class OpenTableParticipantCompleteDTO extends OpenTableParticipantBasicDTO {
	private OpenTableBasicDTO openTable;
}
