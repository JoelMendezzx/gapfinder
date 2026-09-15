package com.backend.gapfinder.entities.rating;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.match.MatchEntity;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que representa una calificación dejada al finalizar un Match o una Open Table
@Entity
@Table(name = "ratings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RatingEntity extends BaseEntity {

    private int rating;

    private Boolean wouldRepeat;

    private LocalDateTime createdAt;

    // Usuario que otorga la calificación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity rater;

    // Usuario calificado (nullable si es una calificación grupal sin persona específica)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity ratedUser;

    // Match calificado (nullable si la calificación es de una Open Table)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MatchEntity match;

    // Open Table calificada (nullable si la calificación es de un Match)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_table_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OpenTableEntity openTable;

}