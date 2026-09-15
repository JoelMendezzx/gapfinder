package com.backend.gapfinder.entities.building;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.userlocationlog.UserLocationLogEntity;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;
import com.backend.gapfinder.entities.user.UserEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

// Entidad que representa un edificio del campus (ML, SD, Library, etc.)
@Entity
@Table(name = "buildings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BuildingEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    private double latitude;

    private double longitude;

    private double radiusMeters;

    // Usuarios que están actualmente en este building (lado inverso)
    @OneToMany(mappedBy = "currentBuilding", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserEntity> currentUsers = new ArrayList<>();

    // Open Tables ubicadas en este building
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OpenTableEntity> openTables = new ArrayList<>();

    // Registros de ubicación (checkpoints) hechos en este building
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserLocationLogEntity> locationLogs = new ArrayList<>();

}