package com.backend.gapfinder.entities.notification;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    // Genera una nueva notificación para un usuario
    @Transactional
    public NotificationEntity create(Long userId, NotificationTypeEnum type, Long referenceId, String message) {
        log.info("Inicia proceso de creación de notificación para el usuario con id = {}", userId);

        UserEntity user = userService.getById(userId);
        validateNotificationData(type, message);

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        log.info("Termina proceso de creación de notificación para el usuario con id = {}", userId);
        return notificationRepository.save(notification);
    }

    // Consulta todas las notificaciones de un usuario, más recientes primero
    @Transactional
    public List<NotificationEntity> getAllByUser(Long userId) {
        log.info("Inicia proceso de consultar notificaciones del usuario con id = {}", userId);

        userService.getById(userId); // valida que el usuario exista

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Marca una notificación puntual como leída
    @Transactional
    public NotificationEntity markAsRead(Long id) {
        log.info("Inicia proceso de marcar como leída la notificación con id = {}", id);

        NotificationEntity notification = getById(id);
        notification.setRead(true);

        log.info("Termina proceso de marcar como leída la notificación con id = {}", id);
        return notificationRepository.save(notification);
    }

    // Cuenta las notificaciones no leídas de un usuario
    @Transactional
    public long getUnreadCount(Long userId) {
        log.info("Inicia proceso de contar notificaciones no leídas del usuario con id = {}", userId);

        userService.getById(userId); // valida que el usuario exista

        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // Busca una notificación por id o lanza NotFoundException
    @Transactional
    public NotificationEntity getById(Long id) {
        log.info("Inicia proceso de consultar la notificación con id = {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La notificación con id " + id + " no existe"));
    }

    // Valida que el tipo y el mensaje de la notificación sean correctos
    private void validateNotificationData(NotificationTypeEnum type, String message) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo de notificación es obligatorio");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("El mensaje de la notificación es obligatorio");
        }
        if (message.length() > 500) {
            throw new IllegalArgumentException("El mensaje no puede superar los 500 caracteres");
        }
    }
}