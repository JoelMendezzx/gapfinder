package com.backend.gapfinder.entities.userlocationlog;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.building.BuildingEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que registra un checkpoint de ubicación de un usuario durante un GAP
@Entity
@Table(name = "user_location_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserLocationLogEntity extends BaseEntity {

    private double latitude;

    private double longitude;

    private LocalDateTime recordedAt;

    // Usuario al que pertenece este checkpoint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    // GAP durante el cual se registró este checkpoint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapEntity gap;

    // Building resuelto por el backend a partir de latitude/longitude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingEntity building;

}