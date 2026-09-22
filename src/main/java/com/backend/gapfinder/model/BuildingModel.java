package com.backend.gapfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BuildingModel extends BaseModel {

    @Column(unique = true, nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    @Column(
        name = "location",
        columnDefinition = "geography(Point,4326)",
        nullable = false
    )
    private Point location;

    @Column(name = "radius_meters", nullable = false)
    private double radiusMeters;

    @OneToMany(mappedBy = "currentBuilding", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserModel> currentUsers = new ArrayList<>();

    @OneToMany(
        mappedBy = "building",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OpenTableModel> openTables = new ArrayList<>();

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserLocationLogModel> locationLogs = new ArrayList<>();
}