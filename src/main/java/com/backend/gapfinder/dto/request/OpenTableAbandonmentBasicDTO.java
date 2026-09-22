package com.backend.gapfinder.dto.request;

import com.backend.gapfinder.enums.OpenTableCreationStep;
import jakarta.validation.constraints.NotNull;

public record OpenTableAbandonmentBasicDTO(
    @NotNull Long userId,
    @NotNull OpenTableCreationStep step,
    Long activityId,
    Integer durationMinutes,
    Long buildingId
) {}