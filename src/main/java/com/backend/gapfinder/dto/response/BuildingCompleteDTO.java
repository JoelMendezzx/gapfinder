package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BuildingCompleteDTO extends BuildingBasicDTO {
	private List<UserBasicDTO> currentUsers;
	private List<OpenTableBasicDTO> openTables;
	private List<UserLocationLogBasicDTO> locationLogs;
}
