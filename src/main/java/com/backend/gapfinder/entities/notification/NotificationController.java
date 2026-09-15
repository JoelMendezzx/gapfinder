package com.backend.gapfinder.entities.notification;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public NotificationController(NotificationService notificationService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    // Crea una nueva notificación
    // POST /notifications?userId=1&type=MATCH_REQUEST&referenceId=7&message=Hola
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationBasicDTO createNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam(required = false) Long referenceId,
            @RequestParam String message) {

        NotificationEntity notification = notificationService.create(
                userId,
                com.backend.gapfinder.enums.NotificationTypeEnum.valueOf(type),
                referenceId,
                message
        );

        return modelMapper.map(notification, NotificationBasicDTO.class);
    }

    // Obtiene todas las notificaciones de un usuario
    // GET /notifications/user/{userId}
    @GetMapping("/user/{userId}")
    public List<NotificationBasicDTO> getNotificationsByUser(@PathVariable Long userId) {
        List<NotificationEntity> notifications = notificationService.getAllByUser(userId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationBasicDTO>>() {}.getType());
    }

    // Cuenta notificaciones no leídas
    // GET /notifications/unread-count?userId=1
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    // Obtiene una notificación por id
    // GET /notifications/{id}
    @GetMapping("/{id}")
    public NotificationCompleteDTO getNotification(@PathVariable Long id) {
        NotificationEntity notification = notificationService.getById(id);
        return modelMapper.map(notification, NotificationCompleteDTO.class);
    }

    // Marca una notificación como leída
    // PATCH /notifications/{id}/read
    @PatchMapping("/{id}/read")
    public NotificationBasicDTO markAsRead(@PathVariable Long id) {
        NotificationEntity notification = notificationService.markAsRead(id);
        return modelMapper.map(notification, NotificationBasicDTO.class);
    }
}
