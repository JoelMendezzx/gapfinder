package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.NotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {

    // Devuelve todas las notificaciones de un usuario, de la más reciente a la más antigua
    List<NotificationModel> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // Devuelve solo las notificaciones no leídas de un usuario
    List<NotificationModel> findByUser_IdAndReadFalse(Long userId);

    // Cuenta cuántas notificaciones no leídas tiene un usuario (para el badge/contador)
    long countByUser_IdAndReadFalse(Long userId);
}