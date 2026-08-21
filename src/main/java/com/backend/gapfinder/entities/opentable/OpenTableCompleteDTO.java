package com.backend.gapfinder.entities.opentable;

import com.backend.gapfinder.entities.activity.ActivityBasicDTO;
import com.backend.gapfinder.entities.building.BuildingBasicDTO;
import com.backend.gapfinder.entities.group.GroupBasicDTO;
import com.backend.gapfinder.entities.message.MessageBasicDTO;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class OpenTableCompleteDTO extends OpenTableBasicDTO {
	private ActivityBasicDTO activity;
	private UserBasicDTO creator;
	private BuildingBasicDTO building;
	private GroupBasicDTO group;
	private List<MessageBasicDTO> messages;
	private List<OpenTableParticipantBasicDTO> participants;
}
