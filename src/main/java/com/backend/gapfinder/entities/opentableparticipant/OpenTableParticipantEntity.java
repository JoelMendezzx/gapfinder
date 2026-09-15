package com.backend.gapfinder.entities.opentableparticipant;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que representa la participación (RSVP) de un usuario en una Open Table
@Entity
@Table(name = "open_table_participants")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OpenTableParticipantEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ResponseStatusEnum rsvp;

    private LocalDateTime respondedAt;

    // Open Table a la que pertenece esta participación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_table_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OpenTableEntity openTable;

    // Usuario que participa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity user;

}