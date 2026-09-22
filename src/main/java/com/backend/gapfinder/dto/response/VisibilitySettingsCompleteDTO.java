package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class VisibilitySettingsCompleteDTO extends VisibilitySettingsBasicDTO {
    private UserBasicDTO user;
}
