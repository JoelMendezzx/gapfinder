package com.backend.gapfinder.entities.building;

import com.backend.gapfinder.entities.opentable.OpenTableBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import com.backend.gapfinder.entities.userlocationlog.UserLocationLogBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class BuildingCompleteDTO extends BuildingBasicDTO {
	private List<UserBasicDTO> currentUsers;
	private List<OpenTableBasicDTO> openTables;
	private List<UserLocationLogBasicDTO> locationLogs;
}
