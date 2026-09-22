package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class FriendshipCompleteDTO extends FriendshipBasicDTO {
	private UserBasicDTO requester;
	private UserBasicDTO addressee;
}
