package com.backend.gapfinder.entities.analytics;

import com.backend.gapfinder.enums.VisibilityScopeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

// BQ 9: conexiones aceptadas segun la visibilidad con que se publico el GAP
@Data
@AllArgsConstructor
public class VisibilityOutcomeDTO {
    private VisibilityScopeEnum visibilityScope;
    private long publishedGaps;
    private long acceptedConnections;
    // Conexiones aceptadas por GAP publicado: permite comparar opciones
    // con distinta cantidad de GAPs
    private double acceptedPerGap;
}
