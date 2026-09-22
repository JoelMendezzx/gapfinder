package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.request.OpenTableAbandonmentBasicDTO;
import com.backend.gapfinder.dto.response.OpenTableAbandonmentStatsBasicDTO;
import com.backend.gapfinder.model.OpenTableAbandonmentModel;
import com.backend.gapfinder.service.OpenTableAbandonmentService;
// package: ajústalo al de tus controllers

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/open-table-abandonments")
public class OpenTableAbandonmentController {

    private final OpenTableAbandonmentService abandonmentService;

    public OpenTableAbandonmentController(OpenTableAbandonmentService abandonmentService) {
        this.abandonmentService = abandonmentService;
    }

    // Registrar en qué paso del formulario abandonó el usuario
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody OpenTableAbandonmentBasicDTO dto) {
        abandonmentService.create(dto.userId(), toModel(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Abandonos, personas que llegaron y tasa de abandono de cada paso desde la fecha dada
    @GetMapping("/stats")
    public ResponseEntity<List<OpenTableAbandonmentStatsBasicDTO>> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(abandonmentService.calculateAbandonmentRateByStep(since));
    }

    // Paso con mayor tasa de abandono desde la fecha dada (204 si aún no hay abandonos)
    @GetMapping("/stats/most-abandoned")
    public ResponseEntity<OpenTableAbandonmentStatsBasicDTO> getMostAbandonedStep(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return abandonmentService.getMostAbandonedStep(since)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private OpenTableAbandonmentModel toModel(OpenTableAbandonmentBasicDTO dto) {
        OpenTableAbandonmentModel abandonment = new OpenTableAbandonmentModel();
        abandonment.setStep(dto.step());
        abandonment.setActivityId(dto.activityId());
        abandonment.setDurationMinutes(dto.durationMinutes());
        abandonment.setBuildingId(dto.buildingId());
        return abandonment;
    }
}