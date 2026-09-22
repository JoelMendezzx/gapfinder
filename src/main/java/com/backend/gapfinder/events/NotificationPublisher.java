package com.backend.gapfinder.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Component
public class NotificationPublisher {

    private final List<NotificationListener> listeners;

    public NotificationPublisher(List<NotificationListener> listeners) {
        this.listeners = listeners;
    }

    // Publica el evento. Si se llama dentro de una transacción activa,
    // espera a que haga commit antes de notificar a los listeners; así
    // nunca se notifica algo que luego termina revertido.
    public void publish(NotificationEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatch(event);
                }
            });
        } else {
            dispatch(event);
        }
    }

    // Notifica a cada listener por separado. Si uno falla, se registra el
    // error pero NO afecta a los demás listeners ni al proceso que publicó
    // el evento (el match, el gap, etc. ya se guardaron correctamente).
    private void dispatch(NotificationEvent event) {
        log.info("Publicando evento {} para el usuario {}", event.type(), event.userId());
        for (NotificationListener listener : listeners) {
            try {
                listener.onNotificationEvent(event);
            } catch (Exception ex) {
                log.error("Error notificando al listener {} para el evento {}: {}",
                        listener.getClass().getSimpleName(), event.type(), ex.getMessage(), ex);
            }
        }
    }
}