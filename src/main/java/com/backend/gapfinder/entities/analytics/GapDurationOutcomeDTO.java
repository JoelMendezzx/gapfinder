package com.backend.gapfinder.entities.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

// BQ 5: como le fue a los GAPs de una duracion dada
@Data
@AllArgsConstructor
public class GapDurationOutcomeDTO {
    private int durationMinutes;
    private long publishedGaps;
    private long gapsWithMatch;
    private long gapsWithoutMatch;
    // Porcentaje de GAPs de esta duracion que terminaron sin match
    private double withoutMatchRate;
}
