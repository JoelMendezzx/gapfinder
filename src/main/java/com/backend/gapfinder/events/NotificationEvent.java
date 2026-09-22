package com.backend.gapfinder.events;

import com.backend.gapfinder.enums.NotificationTypeEnum;
public record NotificationEvent(
        Long userId,
        NotificationTypeEnum type,
        Long referenceId,
        String message
) {}
