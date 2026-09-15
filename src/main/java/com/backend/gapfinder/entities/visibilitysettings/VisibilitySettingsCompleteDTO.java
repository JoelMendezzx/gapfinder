package com.backend.gapfinder.entities.visibilitysettings;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class VisibilitySettingsCompleteDTO extends VisibilitySettingsBasicDTO {
    private UserBasicDTO user;
}
