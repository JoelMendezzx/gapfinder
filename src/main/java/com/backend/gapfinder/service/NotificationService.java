package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.BaseModel;
import com.backend.gapfinder.model.NotificationModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.NotificationRepository;
import com.backend.gapfinder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                                UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Crea y persiste una nueva notificación para un usuario
    public NotificationModel create(Long userId, NotificationTypeEnum type, Long referenceId, String message) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        NotificationModel notification = new NotificationModel();
        notification.setUser(user);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setTitle(resolveTitle(type));
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now()); // quitar si BaseModel ya lo maneja

        NotificationModel saved = notificationRepository.save(notification);
        log.info("Notification {} created for user {}", type, userId);
        return saved;
    }

    // Busca una notificación por id, lanza NotFoundException si no existe
    public NotificationModel getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La notificación con id " + id + " no existe"));
    }

    // Devuelve todas las notificaciones de un usuario, de la más reciente a la más antigua
    public List<NotificationModel> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    // Cuenta las notificaciones no leídas de un usuario (para el badge)
    public long countUnread(Long userId) {
        return notificationRepository.countByUser_IdAndReadFalse(userId);
    }

    // Marca una notificación como leída y devuelve la entidad ya actualizada
    public NotificationModel markAsRead(Long notificationId) {
        NotificationModel notification = getById(notificationId);
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    // Marca todas las notificaciones no leídas de un usuario como leídas
    public void markAllAsRead(Long userId) {
        List<NotificationModel> unread = notificationRepository.findByUser_IdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    // Traduce el tipo de notificación a un título legible en inglés
    private String resolveTitle(NotificationTypeEnum type) {
        return switch (type) {
            case MATCH_REQUEST -> "New match request";
            case MATCH_ACCEPTED -> "Match accepted!";
            case MATCH_REJECTED -> "Match rejected";
            case MATCH_CLOSED -> "Your match has ended";
            case MESSAGE_RECEIVED -> "New message";
            case FRIEND_REQUEST -> "Friend request";
            case FRIEND_ACCEPTED -> "Friend request accepted";
            case OPEN_TABLE_JOIN -> "Someone joined your table";
            case GROUP_GAP_AVAILABLE -> "New group gap available";
            case GAP_STARTING_SOON -> "Your gap is starting soon";
            case GAP_STARTED -> "Your gap has started";
            case GAP_ENDING_SOON -> "Your gap is ending soon";
            case GAP_ENDED -> "Your gap has ended";
        };
    }
}