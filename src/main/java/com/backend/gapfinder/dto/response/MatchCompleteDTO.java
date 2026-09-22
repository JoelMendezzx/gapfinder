package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MatchCompleteDTO extends MatchBasicDTO {
	private ActivityBasicDTO chosenActivity;
	private UserBasicDTO requester;
	private UserBasicDTO receiver;
	private GapBasicDTO requesterGap;
	private GapBasicDTO receiverGap;
	private List<MessageBasicDTO> messages;
}
