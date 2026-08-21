package com.backend.gapfinder.entities.opentable;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.group.GroupEntity;
import com.backend.gapfinder.entities.message.MessageEntity;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantEntity;
import com.backend.gapfinder.entities.activity.ActivityEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa una Open Table: actividad pública o privada de grupo
@Entity
@Table(name = "open_tables")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OpenTableEntity extends BaseEntity {

    // Actividad del catálogo asociada a la Open Table
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ActivityEntity activity;

    @Column(length = 2000)
    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(name = "is_private")
    private boolean privateTable;

    @Enumerated(EnumType.STRING)
    private OpenTableStatusEnum status;

    private LocalDateTime createdAt;

    // Usuario que creó la Open Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity creator;

    // Building donde se ubica la Open Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingEntity building;

    // Grupo dueño de la Open Table, si es privada (null si es pública)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GroupEntity group;

    // Mensajes del chat de esta Open Table
    @OneToMany(mappedBy = "openTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MessageEntity> messages = new ArrayList<>();

    // Participantes con su estado de RSVP
    @OneToMany(mappedBy = "openTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OpenTableParticipantEntity> participants = new ArrayList<>();

}