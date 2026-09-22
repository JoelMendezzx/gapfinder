package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.MatchStatusEnum;
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
public class MatchModel extends BaseModel {

    @Enumerated(EnumType.STRING)
    private MatchStatusEnum status;

    private LocalDateTime overlapStart;

    private LocalDateTime overlapEnd;

    private LocalDateTime createdAt;

    // Actividad sugerida automáticamente al aceptar el match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chosen_activity_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityModel chosenActivity;

    // Usuario que envió la solicitud de match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel requester;

    // Usuario que recibió la solicitud de match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel receiver;

    // GAP del requester durante el cual se generó el match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapModel requesterGap;

    // GAP del receiver durante el cual se generó el match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapModel receiverGap;

    // Si el requester repetiría su GAP con esta persona (null = sin responder)
    @Column(name = "requester_wants_rematch")
    private Boolean requesterWantsRematch;

    // Si el receiver repetiría su GAP con esta persona (null = sin responder)
    @Column(name = "receiver_wants_rematch")
    private Boolean receiverWantsRematch;

    // Mensajes del chat de este match
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MessageModel> messages = new ArrayList<>();

}