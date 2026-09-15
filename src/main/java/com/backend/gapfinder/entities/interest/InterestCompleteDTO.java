package com.backend.gapfinder.entities.interest;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class InterestCompleteDTO extends InterestBasicDTO {
	private List<UserBasicDTO> users;
}
