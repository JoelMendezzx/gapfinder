package com.backend.gapfinder.entities.user;

import com.backend.gapfinder.entities.building.BuildingBasicDTO;
import com.backend.gapfinder.entities.classblock.ClassBlockBasicDTO;
import com.backend.gapfinder.entities.friendship.FriendshipBasicDTO;
import com.backend.gapfinder.entities.gap.GapBasicDTO;
import com.backend.gapfinder.entities.group.GroupBasicDTO;
import com.backend.gapfinder.entities.interest.InterestBasicDTO;
import com.backend.gapfinder.entities.visibilitysettings.VisibilitySettingsBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserCompleteDTO extends UserBasicDTO {
	private BuildingBasicDTO currentBuilding;
	private VisibilitySettingsBasicDTO visibilitySettings;
	private List<InterestBasicDTO> interests;
	private List<ClassBlockBasicDTO> classBlocks;
	private List<GapBasicDTO> gaps;
	private List<GroupBasicDTO> createdGroups;
	private List<GroupBasicDTO> groups;
	private List<FriendshipBasicDTO> sentFriendRequests;
	private List<FriendshipBasicDTO> receivedFriendRequests;
}
