package com.backend.gapfinder.service;

import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceListener implements NotificationListener {

    private final NotificationService notificationService;

    public NotificationServiceListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onNotificationEvent(NotificationEvent event) {
        notificationService.create(
                event.userId(),
                event.type(),
                event.referenceId(),
                event.message()
        );
    }
}
