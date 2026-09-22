package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class BuildingTimeDTO {

    private BuildingBasicDTO building;

    private Integer minutes;
}