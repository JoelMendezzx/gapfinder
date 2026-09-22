package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.BuildingFreeTimeStatsBasicDTO;
import com.backend.gapfinder.service.BuildingGapService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/building-gaps")
public class BuildingGapController {

    private final BuildingGapService buildingGapService;

    public BuildingGapController(BuildingGapService buildingGapService) {
        this.buildingGapService = buildingGapService;
    }

    // Minutos de tiempo libre y estudiantes distintos por edificio desde una fecha
    // GET /building-gaps/stats?since=2026-09-01T00:00:00
    @GetMapping("/stats")
    public List<BuildingFreeTimeStatsBasicDTO> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
    ) {
        return buildingGapService.calculateFreeTimePerBuilding(since);
    }

    // Edificio con más minutos de tiempo libre desde una fecha (null si aún no hay datos)
    // GET /building-gaps/stats/top?since=2026-09-01T00:00:00
    @GetMapping("/stats/top")
    public BuildingFreeTimeStatsBasicDTO getTop(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
    ) {
        return buildingGapService.getBuildingWithMostFreeTime(since).orElse(null);
    }

    // Edificio donde más estudiantes distintos tienen tiempo libre desde una fecha (null si aún no hay datos)
    // Responde: "Where do the most students have free time on campus?"
    // GET /building-gaps/stats/top-students?since=2026-09-01T00:00:00
    @GetMapping("/stats/top-students")
    public BuildingFreeTimeStatsBasicDTO getTopByStudents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
    ) {
        return buildingGapService.getBuildingWithMostStudentsFree(since).orElse(null);
    }
}