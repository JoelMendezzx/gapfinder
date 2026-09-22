package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que representa una notificación enviada a un usuario
@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NotificationModel extends BaseModel {

    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    private Long referenceId;

    @Column(length = 500)
    private String message;

    private String title;


    private boolean read;

    private LocalDateTime createdAt;

    // Usuario al que le llega esta notificación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

}