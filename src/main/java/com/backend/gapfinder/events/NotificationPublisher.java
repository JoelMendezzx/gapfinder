package com.backend.gapfinder.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NotificationPublisher {

    private final List<NotificationListener> listeners;

    public NotificationPublisher(List<NotificationListener> listeners) {
        this.listeners = listeners;
    }

    public void publish(NotificationEvent event) {
        log.info("Publicando evento {} para el usuario {}", event.type(), event.userId());
        for (NotificationListener listener : listeners) {
            listener.onNotificationEvent(event);
        }
    }
}
