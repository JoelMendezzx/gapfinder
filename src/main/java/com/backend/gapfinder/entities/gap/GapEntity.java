package com.backend.gapfinder.entities.gap;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.userlocationlog.UserLocationLogEntity;
import com.backend.gapfinder.entities.user.UserEntity;
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
public class GapEntity extends BaseEntity {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int durationMinutes;

    // Usuario dueño de este GAP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    // Checkpoints de ubicación registrados durante este GAP
    @OneToMany(mappedBy = "gap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserLocationLogEntity> locationLogs = new ArrayList<>();

}