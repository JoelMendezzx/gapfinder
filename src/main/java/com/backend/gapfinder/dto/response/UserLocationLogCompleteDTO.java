package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class UserLocationLogCompleteDTO extends UserLocationLogBasicDTO {
    private UserBasicDTO user;
    private GapBasicDTO gap;
    private BuildingBasicDTO building;
}