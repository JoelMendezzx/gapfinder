package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserCompleteDTO extends UserBasicDTO {
	private VisibilitySettingsBasicDTO visibilitySettings;
	private List<InterestBasicDTO> interests;
	private List<ClassBlockBasicDTO> classBlocks;
	private List<GapBasicDTO> gaps;
	private List<GroupBasicDTO> createdGroups;
	private List<GroupBasicDTO> groups;
	private List<FriendshipBasicDTO> sentFriendRequests;
	private List<FriendshipBasicDTO> receivedFriendRequests;
}
