package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.NotificationBasicDTO;
import com.backend.gapfinder.model.NotificationModel;
import com.backend.gapfinder.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    // GET /notifications/user/{userId}
    @GetMapping("/user/{userId}")
    public List<NotificationBasicDTO> getUserNotifications(@PathVariable Long userId) {
        List<NotificationModel> notifications = notificationService.getUserNotifications(userId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationBasicDTO>>() {}.getType());
    }

    // GET /notifications/unread-count?userId=...
    @GetMapping("/unread-count")
    public long countUnread(@RequestParam Long userId) {
        return notificationService.countUnread(userId);
    }

    // GET /notifications/{id}
    @GetMapping("/{id}")
    public NotificationBasicDTO getNotification(@PathVariable Long id) {
        return modelMapper.map(notificationService.getById(id), NotificationBasicDTO.class);
    }

    // PATCH /notifications/{id}/read
    @PatchMapping("/{id}/read")
    public NotificationBasicDTO markAsRead(@PathVariable Long id) {
        return modelMapper.map(notificationService.markAsRead(id), NotificationBasicDTO.class);
    }

    // PATCH /notifications/user/{userId}/read-all
    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}