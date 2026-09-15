package com.backend.gapfinder.entities.user;

import com.backend.gapfinder.enums.ActivityEffortEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBasicDTO {
	private Long id;
	private String name;
	private String email;
	private String program;
	private String semester;
	private String avatarUrl;
	private boolean verified;
	private ActivityEffortEnum activityEffortPreference;
	private LocalDateTime locationUpdatedAt;
	private LocalDateTime createdAt;
}
