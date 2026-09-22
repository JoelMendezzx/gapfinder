package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class NotificationCompleteDTO extends NotificationBasicDTO {
    private UserBasicDTO user;
}
