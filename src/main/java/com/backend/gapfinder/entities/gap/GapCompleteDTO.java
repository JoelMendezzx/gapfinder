package com.backend.gapfinder.entities.gap;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import com.backend.gapfinder.entities.userlocationlog.UserLocationLogBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class GapCompleteDTO extends GapBasicDTO {
	private UserBasicDTO user;
	private List<UserLocationLogBasicDTO> locationLogs;
}
