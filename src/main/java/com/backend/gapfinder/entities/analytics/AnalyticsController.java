package com.backend.gapfinder.entities.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Endpoints que responden las Business Questions del proyecto
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // BQ 5: que duraciones de GAP terminan mas seguido sin match
    // GET /analytics/gap-durations-without-match
    @GetMapping("/gap-durations-without-match")
    public List<GapDurationOutcomeDTO> gapDurationsWithoutMatch() {
        return analyticsService.gapDurationsWithoutMatch();
    }

    // BQ 7: planes abiertos contra invitaciones directas
    // GET /analytics/connection-mechanisms
    @GetMapping("/connection-mechanisms")
    public List<ConnectionMechanismDTO> connectionMechanisms() {
        return analyticsService.connectionMechanisms();
    }

    // BQ 9: que opcion de visibilidad genera mas conexiones aceptadas
    // GET /analytics/visibility-outcomes
    @GetMapping("/visibility-outcomes")
    public List<VisibilityOutcomeDTO> visibilityOutcomes() {
        return analyticsService.acceptedConnectionsByVisibility();
    }
}
