package com.backend.gapfinder.dto.response;

import com.backend.gapfinder.enums.OpenTableCreationStep;
public record OpenTableAbandonmentStatsBasicDTO(
    OpenTableCreationStep step,
    long abandonments,
    long reached,
    double abandonmentRate
) {}