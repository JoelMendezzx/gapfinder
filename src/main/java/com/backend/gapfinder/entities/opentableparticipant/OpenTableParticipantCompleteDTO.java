package com.backend.gapfinder.entities.opentableparticipant;

import com.backend.gapfinder.entities.opentable.OpenTableBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class OpenTableParticipantCompleteDTO extends OpenTableParticipantBasicDTO {
	private OpenTableBasicDTO openTable;
	private UserBasicDTO user;
}
