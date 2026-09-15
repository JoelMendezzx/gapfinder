package com.backend.gapfinder.entities.building;

import lombok.Data;

@Data
public class BuildingTimeDTO {

    private BuildingBasicDTO building;

    private Integer minutes;
}