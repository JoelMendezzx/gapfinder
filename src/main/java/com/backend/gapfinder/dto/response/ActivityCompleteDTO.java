package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class ActivityCompleteDTO extends ActivityBasicDTO {
    private InterestBasicDTO interest;
}
