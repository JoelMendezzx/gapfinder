package com.backend.gapfinder.entities.userlocationlog;

import com.backend.gapfinder.entities.building.BuildingBasicDTO;
import com.backend.gapfinder.entities.gap.GapBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class UserLocationLogCompleteDTO extends UserLocationLogBasicDTO {
    private UserBasicDTO user;
    private GapBasicDTO gap;
    private BuildingBasicDTO building;
}