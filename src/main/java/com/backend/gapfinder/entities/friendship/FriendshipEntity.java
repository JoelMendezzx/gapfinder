package com.backend.gapfinder.entities.friendship;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.FriendshipStatusEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que representa una relación de amistad entre dos usuarios
@Entity
@Table(name = "friendships")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FriendshipEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private FriendshipStatusEnum status;

    private LocalDateTime createdAt;

    // Usuario que envió la solicitud de amistad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity requester;

    // Usuario que recibió la solicitud de amistad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity addressee;

}