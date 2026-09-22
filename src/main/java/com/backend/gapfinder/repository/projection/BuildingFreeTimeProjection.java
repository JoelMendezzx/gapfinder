package com.backend.gapfinder.repository.projection;

public interface BuildingFreeTimeProjection {

    Long getBuildingId();

    Long getTotalMinutes();

    Long getStudents();
}