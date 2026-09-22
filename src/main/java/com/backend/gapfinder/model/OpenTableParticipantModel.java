package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.ResponseStatusEnum;
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
public class OpenTableParticipantModel extends BaseModel {

    @Enumerated(EnumType.STRING)
    private ResponseStatusEnum rsvp;

    private LocalDateTime respondedAt;

    // Si a este participante le gustó la Open Table, una vez finalizada (null = sin responder)
    @Column(name = "enjoyed")
    private Boolean enjoyed;

    // Open Table a la que pertenece esta participación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_table_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OpenTableModel openTable;

    // Usuario que participa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

}