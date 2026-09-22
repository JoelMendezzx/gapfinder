package com.backend.gapfinder.service;

import com.backend.gapfinder.dto.response.BuildingFreeTimeStatsBasicDTO;
import com.backend.gapfinder.repository.UserLocationLogRepository;
import com.backend.gapfinder.repository.projection.BuildingFreeTimeProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BuildingGapService {

    private final UserLocationLogRepository userLocationLogRepository;
    private final BuildingService buildingService;

    public BuildingGapService(UserLocationLogRepository userLocationLogRepository,
                              BuildingService buildingService) {
        this.userLocationLogRepository = userLocationLogRepository;
        this.buildingService = buildingService;
    }

    // Calcular, por building, los minutos totales de tiempo libre y los estudiantes distintos desde una fecha.
    // Incluye los buildings sin datos (0 minutos) y los ordena de mayor a menor tiempo libre.
    @Transactional(readOnly = true)
    public List<BuildingFreeTimeStatsBasicDTO> calculateFreeTimePerBuilding(LocalDateTime since) {
        log.info("Inicia proceso de calcular tiempo libre por building desde {}", since);

        Map<Long, BuildingFreeTimeProjection> porBuilding = userLocationLogRepository
                .findFreeTimePerBuilding(since)
                .stream()
                .collect(Collectors.toMap(BuildingFreeTimeProjection::getBuildingId, Function.identity()));

        List<BuildingFreeTimeStatsBasicDTO> stats = buildingService.getAll().stream()
                .map(building -> {
                    BuildingFreeTimeProjection datos = porBuilding.get(building.getId());

                    return new BuildingFreeTimeStatsBasicDTO(
                            building.getId(),
                            building.getName(),
                            datos == null ? 0L : datos.getTotalMinutes(),
                            datos == null ? 0L : datos.getStudents()
                    );
                })
                .sorted(Comparator.comparingLong(BuildingFreeTimeStatsBasicDTO::getTotalMinutes)
                        .thenComparingLong(BuildingFreeTimeStatsBasicDTO::getStudents)
                        .reversed())
                .toList();

        log.info("Termina proceso de calcular tiempo libre por building desde {}", since);
        return stats;
    }

    // Obtener el building con más minutos de tiempo libre desde una fecha (vacío si aún no hay datos)
    @Transactional(readOnly = true)
    public Optional<BuildingFreeTimeStatsBasicDTO> getBuildingWithMostFreeTime(LocalDateTime since) {
        return calculateFreeTimePerBuilding(since).stream()
                .filter(stat -> stat.getTotalMinutes() > 0)
                .findFirst();
    }

    // Obtener el building donde más estudiantes distintos tienen tiempo libre desde una fecha (vacío si aún no hay datos)
    @Transactional(readOnly = true)
    public Optional<BuildingFreeTimeStatsBasicDTO> getBuildingWithMostStudentsFree(LocalDateTime since) {
        log.info("Inicia proceso de buscar el building con más estudiantes libres desde {}", since);

        Optional<BuildingFreeTimeStatsBasicDTO> top = calculateFreeTimePerBuilding(since).stream()
                .filter(stat -> stat.getStudents() > 0)
                .max(Comparator.comparingLong(BuildingFreeTimeStatsBasicDTO::getStudents)
                        .thenComparingLong(BuildingFreeTimeStatsBasicDTO::getTotalMinutes));

        log.info("Termina proceso de buscar el building con más estudiantes libres desde {}", since);
        return top;
    }
}


