package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class InterestCompleteDTO extends InterestBasicDTO {
	private List<UserBasicDTO> users;
}
