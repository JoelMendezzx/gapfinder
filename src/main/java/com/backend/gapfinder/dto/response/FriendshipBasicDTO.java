package com.backend.gapfinder.dto.response;

import com.backend.gapfinder.enums.FriendshipStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendshipBasicDTO {
	private Long id;
	private FriendshipStatusEnum status;
	private LocalDateTime createdAt;
}
