package com.backend.gapfinder.entities.group;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa un grupo de amigos creado por un usuario
@Entity
@Table(name = "groups")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GroupEntity extends BaseEntity {

    private String name;

    private LocalDateTime createdAt;

    // Usuario que creó el grupo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity creator;

    // Miembros del grupo (relación many-to-many, dueña de la tabla intermedia group_member)
    @ManyToMany
    @JoinTable(
        name = "group_member",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserEntity> members = new ArrayList<>();

    // Open Tables privadas creadas por este grupo
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OpenTableEntity> openTables = new ArrayList<>();

}