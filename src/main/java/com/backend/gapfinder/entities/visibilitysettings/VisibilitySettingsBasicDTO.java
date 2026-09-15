package com.backend.gapfinder.entities.visibilitysettings;

import com.backend.gapfinder.enums.VisibilityScopeEnum;
import lombok.Data;

@Data
public class VisibilitySettingsBasicDTO {
    private Long id;
    private VisibilityScopeEnum visibilityScope;
    private boolean showSchedule;
    private boolean showInterests;
    private boolean showGap;

}
