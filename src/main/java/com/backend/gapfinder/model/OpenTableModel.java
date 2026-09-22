package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa una Open Table pública.
@Entity
@Table(name = "open_tables")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OpenTableModel extends BaseModel {

    // Actividad del catálogo asociada a la Open Table
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityModel activity;

    @Column(length = 2000)
    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private OpenTableStatusEnum status;

    private LocalDateTime createdAt;

    // Usuario que creó la Open Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel creator;

    // Building donde se ubica la Open Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingModel building;

    // Mensajes del chat de esta Open Table
    @OneToMany(mappedBy = "openTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MessageModel> messages = new ArrayList<>();

    // Participantes con su estado de RSVP
    @OneToMany(mappedBy = "openTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OpenTableParticipantModel> participants = new ArrayList<>();

}