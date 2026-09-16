package com.backend.gapfinder.entities.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

// BQ 7: desempeño de un mecanismo de conexion
@Data
@AllArgsConstructor
public class ConnectionMechanismDTO {
    // OPEN_PLAN (Open Tables) o DIRECT_INVITATION (matches)
    private String mechanism;
    private long invitations;
    private long accepted;
    private long rejected;
    private long pending;
    // Aceptadas sobre las que ya fueron respondidas
    private double acceptanceRate;
}
