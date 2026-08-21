package com.backend.gapfinder.entities.match;

import com.backend.gapfinder.entities.activity.ActivityBasicDTO;
import com.backend.gapfinder.entities.gap.GapBasicDTO;
import com.backend.gapfinder.entities.message.MessageBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class MatchCompleteDTO extends MatchBasicDTO {
	private ActivityBasicDTO requesterChosenActivity;
	private ActivityBasicDTO receiverChosenActivity;
	private ActivityBasicDTO chosenActivity;
	private UserBasicDTO requester;
	private UserBasicDTO receiver;
	private GapBasicDTO requesterGap;
	private GapBasicDTO receiverGap;
	private List<MessageBasicDTO> messages;
}
