package com.backend.gapfinder.entities.notification;

import com.backend.gapfinder.enums.NotificationTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationBasicDTO {
    private Long id;
    private NotificationTypeEnum type;
    private Long referenceId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
