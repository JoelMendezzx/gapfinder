package com.backend.gapfinder.entities.match;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.entities.activity.ActivityEntity;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.message.MessageEntity;
import com.backend.gapfinder.entities.rating.RatingEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa un match entre dos usuarios durante un GAP compartido
@Entity
@Table(name = "matches")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MatchEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MatchStatusEnum status;

    private LocalDateTime overlapStart;

    private LocalDateTime overlapEnd;

    private LocalDateTime createdAt;

    // Actividad propuesta por el requester
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_chosen_activity_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityEntity requesterChosenActivity;

    // Actividad propuesta por el receiver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_chosen_activity_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityEntity receiverChosenActivity;

    // Actividad final sorteada entre las dos propuestas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chosen_activity_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityEntity chosenActivity;

    // Usuario que envió la solicitud de match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity requester;

    // Usuario que recibió la solicitud de match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity receiver;

    // GAP del requester durante el cual se generó el match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapEntity requesterGap;

    // GAP del receiver durante el cual se generó el match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapEntity receiverGap;

    // Mensajes del chat de este match
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MessageEntity> messages = new ArrayList<>();

    // Calificaciones dejadas sobre este match
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RatingEntity> ratings = new ArrayList<>();

}