package com.backend.gapfinder.entities.activity;

import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.enums.ActivityEffortEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    // Nivel de esfuerzo que exige la actividad; se compara contra la
    // preferencia de esfuerzo de los usuarios al recomendar actividades
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_effort_level")
    private ActivityEffortEnum activityEffortLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private InterestEntity interest;
}