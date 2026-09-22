package com.backend.gapfinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingFreeTimeStatsBasicDTO {

    private Long buildingId;
    private String buildingName;
    private long totalMinutes;
    private long students;
}