package com.backend.gapfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_location_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserLocationLogModel extends BaseModel {

    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    @Column(
        name = "location",
        columnDefinition = "geography(Point,4326)",
        nullable = false
    )
    private Point location;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gap_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GapModel gap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BuildingModel building;
}