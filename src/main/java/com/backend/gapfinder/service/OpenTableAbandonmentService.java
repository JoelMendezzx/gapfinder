package com.backend.gapfinder.service;

import com.backend.gapfinder.dto.response.OpenTableAbandonmentStatsBasicDTO;
import com.backend.gapfinder.enums.OpenTableCreationStep;
import com.backend.gapfinder.model.OpenTableAbandonmentModel;
import com.backend.gapfinder.repository.OpenTableAbandonmentRepository;
// package: ajústalo al de tus services

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenTableAbandonmentService {

    private final OpenTableAbandonmentRepository abandonmentRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final BuildingService buildingService;
    private final OpenTableService openTableService;

    public OpenTableAbandonmentService(OpenTableAbandonmentRepository abandonmentRepository,
                                       UserService userService,
                                       ActivityService activityService,
                                       BuildingService buildingService,
                                       OpenTableService openTableService) {
        this.abandonmentRepository = abandonmentRepository;
        this.userService = userService;
        this.activityService = activityService;
        this.buildingService = buildingService;
        this.openTableService = openTableService;
    }

    @Transactional
    public OpenTableAbandonmentModel create(Long userId, OpenTableAbandonmentModel abandonment) {
        validateAbandonmentData(abandonment);
        abandonment.setUser(userService.getById(userId));
        return abandonmentRepository.save(abandonment);
    }

    private void validateAbandonmentData(OpenTableAbandonmentModel abandonment) {
        if (abandonment.getStep() == null) {
            throw new IllegalArgumentException("El paso del abandono es obligatorio");
        }
        if (abandonment.getStep() != OpenTableCreationStep.ACTIVITY && abandonment.getActivityId() == null) {
            throw new IllegalArgumentException(
                    "Si el abandono fue después del paso ACTIVITY, la actividad ya debía estar seleccionada");
        }
        if (abandonment.getDurationMinutes() != null) {
            if (abandonment.getActivityId() == null) {
                throw new IllegalArgumentException("La duración va ligada a una actividad seleccionada");
            }
            if (abandonment.getDurationMinutes() <= 0) {
                throw new IllegalArgumentException("La duración debe ser mayor a 0");
            }
        }
        if (abandonment.getBuildingId() != null && abandonment.getStep() != OpenTableCreationStep.LOCATION) {
            throw new IllegalArgumentException("Solo se puede tener edificio si el abandono fue en el paso LOCATION");
        }
        if (abandonment.getActivityId() != null) {
            activityService.getById(abandonment.getActivityId());
        }
        if (abandonment.getBuildingId() != null) {
            buildingService.getById(abandonment.getBuildingId());
        }
    }

    @Transactional(readOnly = true)
    public Map<OpenTableCreationStep, Long> countByStep(LocalDateTime since) {
        Map<OpenTableCreationStep, Long> counts = new EnumMap<>(OpenTableCreationStep.class);
        for (OpenTableCreationStep step : OpenTableCreationStep.values()) {
            counts.put(step, 0L);
        }
        for (Object[] row : abandonmentRepository.countGroupedByStep(since)) {
            counts.put((OpenTableCreationStep) row[0], (Long) row[1]);
        }
        return counts;
    }

    @Transactional(readOnly = true)
    public List<OpenTableAbandonmentStatsBasicDTO> calculateAbandonmentRateByStep(LocalDateTime since) {
        Map<OpenTableCreationStep, Long> counts = countByStep(since);
        long created = openTableService.countCreatedSince(since);
        OpenTableCreationStep[] steps = OpenTableCreationStep.values();

        List<OpenTableAbandonmentStatsBasicDTO> stats = new ArrayList<>();
        for (OpenTableCreationStep step : steps) {
            long abandonments = counts.get(step);

            // Llegaron al paso k: las OpenTables creadas + los abandonos en k o en pasos posteriores
            long reached = created;
            for (OpenTableCreationStep other : steps) {
                if (other.ordinal() >= step.ordinal()) {
                    reached += counts.get(other);
                }
            }

            double rate = reached == 0 ? 0.0 : (double) abandonments / reached;
            stats.add(new OpenTableAbandonmentStatsBasicDTO(step, abandonments, reached, rate));
        }
        return stats;
    }

    @Transactional(readOnly = true)
    public Optional<OpenTableAbandonmentStatsBasicDTO> getMostAbandonedStep(LocalDateTime since) {
        return calculateAbandonmentRateByStep(since).stream()
                .filter(stat -> stat.abandonments() > 0)
                .max(Comparator.comparingDouble(OpenTableAbandonmentStatsBasicDTO::abandonmentRate)
                        .thenComparingLong(OpenTableAbandonmentStatsBasicDTO::abandonments));
    }
}