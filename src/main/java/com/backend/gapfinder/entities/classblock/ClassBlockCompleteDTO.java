package com.backend.gapfinder.entities.classblock;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class ClassBlockCompleteDTO extends ClassBlockBasicDTO {
	private UserBasicDTO user;
}
