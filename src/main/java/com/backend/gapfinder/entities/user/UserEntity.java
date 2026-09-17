package com.backend.gapfinder.entities.user;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.visibilitysettings.VisibilitySettingsEntity;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.classblock.ClassBlockEntity;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.group.GroupEntity;
import com.backend.gapfinder.entities.friendship.FriendshipEntity;
import com.backend.gapfinder.enums.ActivityEffortEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(nullable = false)
    private String program;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = true)
    private String avatarUrl; //opcional

    private boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityEffortEnum activityEffortPreference;

    private LocalDateTime locationUpdatedAt;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_building_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingEntity currentBuilding;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private VisibilitySettingsEntity visibilitySettings;

    @ManyToMany
    @JoinTable(
        name = "user_interest",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InterestEntity> interests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ClassBlockEntity> classBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GapEntity> gaps = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GroupEntity> createdGroups = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GroupEntity> groups = new ArrayList<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FriendshipEntity> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "addressee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FriendshipEntity> receivedFriendRequests = new ArrayList<>();
}