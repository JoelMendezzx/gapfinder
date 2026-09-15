package com.backend.gapfinder.entities.activity;

import com.backend.gapfinder.entities.interest.InterestBasicDTO;
import lombok.Data;

@Data
public class ActivityCompleteDTO extends ActivityBasicDTO {
    private InterestBasicDTO interest;
}
