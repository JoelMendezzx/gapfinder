package com.backend.gapfinder.entities.notification;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class NotificationCompleteDTO extends NotificationBasicDTO {
    private UserBasicDTO user;
}
