package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.OpenTableCreationStep;
// package: ajústalo al de tus entidades

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "open_table_abandonments")
public class OpenTableAbandonmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpenTableCreationStep step;

    @Column(nullable = false)
    private LocalDateTime abandonedAt;

    // Opcionales: solo se llenan si el usuario ya había llegado a esa parte del formulario
    private Long activityId;

    private Integer durationMinutes;

    private Long buildingId;

    @PrePersist
    void onCreate() {
        if (abandonedAt == null) {
            abandonedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

    public OpenTableCreationStep getStep() { return step; }
    public void setStep(OpenTableCreationStep step) { this.step = step; }

    public LocalDateTime getAbandonedAt() { return abandonedAt; }
    public void setAbandonedAt(LocalDateTime abandonedAt) { this.abandonedAt = abandonedAt; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
}