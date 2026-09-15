package com.backend.gapfinder.entities.friendship;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class FriendshipCompleteDTO extends FriendshipBasicDTO {
	private UserBasicDTO requester;
	private UserBasicDTO addressee;
}
