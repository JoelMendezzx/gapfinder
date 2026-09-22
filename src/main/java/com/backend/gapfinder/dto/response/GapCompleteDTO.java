package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GapCompleteDTO extends GapBasicDTO {
	private UserBasicDTO user;
	private List<UserLocationLogBasicDTO> locationLogs;
}
