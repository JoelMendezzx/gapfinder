package com.backend.gapfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa un hueco (GAP) libre en el horario de un usuario
@Entity
@Table(name = "gaps")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GapModel extends BaseModel {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int durationMinutes;

    // Control de notificaciones ya enviadas para este GAP (evita duplicados en el job)
    private boolean startingSoonNotified;

    private boolean startedNotified;

    private boolean endingSoonNotified;

    private boolean endedNotified;

    // Usuario dueño de este GAP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    // Checkpoints de ubicación registrados durante este GAP
    @OneToMany(mappedBy = "gap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserLocationLogModel> locationLogs = new ArrayList<>();

}