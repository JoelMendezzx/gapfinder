package com.backend.gapfinder.entities.user;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.visibilitysettings.VisibilitySettingsEntity;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.classblock.ClassBlockEntity;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.group.GroupEntity;
import com.backend.gapfinder.entities.friendship.FriendshipEntity;
import com.backend.gapfinder.enums.MobilityPreferenceEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad principal para los usuarios estudiantes.
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    // Datos personales
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;
    private String program;
    private String semester;

    @Column(nullable = false)
    private String avatarUrl;
    private boolean verified;

    // Preferencias y fechas
    @Enumerated(EnumType.STRING)
    private MobilityPreferenceEnum mobilityPreference;

    private LocalDateTime locationUpdatedAt;
    private LocalDateTime createdAt;

    // Última ubicación detectada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_building_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingEntity currentBuilding;

    // Configuración de visibilidad del usuario
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private VisibilitySettingsEntity visibilitySettings;

    // Intereses del usuario
    @ManyToMany
    @JoinTable(
        name = "user_interest",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InterestEntity> interests = new ArrayList<>();

    // Clases registradas
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ClassBlockEntity> classBlocks = new ArrayList<>();

    // Huecos (GAPs) libres
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GapEntity> gaps = new ArrayList<>();

    // Grupos creados por el usuario
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GroupEntity> createdGroups = new ArrayList<>();

    // Grupos a los que pertenece
    @ManyToMany(mappedBy = "members")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GroupEntity> groups = new ArrayList<>();

    // Solicitudes de amistad enviadas
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FriendshipEntity> sentFriendRequests = new ArrayList<>();

    // Solicitudes de amistad recibidas
    @OneToMany(mappedBy = "addressee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FriendshipEntity> receivedFriendRequests = new ArrayList<>();

}