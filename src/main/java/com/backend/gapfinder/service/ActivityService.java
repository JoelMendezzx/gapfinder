package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.repository.ActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final InterestService interestService;

    public ActivityService(ActivityRepository activityRepository, InterestService interestService) {
        this.activityRepository = activityRepository;
        this.interestService = interestService;
    }

    // Consultar una actividad por id
    @Transactional(readOnly = true)
    public ActivityModel getById(Long id) {
        log.info("Inicia proceso de consultar la actividad con id = {}", id);
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La actividad con id " + id + " no existe"));
    }

    // Crear una actividad asociada a un interés existente
    @Transactional
    public ActivityModel create(ActivityModel activity, Long interestId) {
        if (activity == null || activity.getTitle() == null || activity.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título de la actividad es obligatorio");
        }
        if (activity.getDurationMinutes() == null || activity.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("La duración de la actividad debe ser mayor a 0 minutos");
        }
        if (activity.getActivityEffortLevel() == null) {
            throw new IllegalArgumentException("El nivel de esfuerzo de la actividad es obligatorio");
        }
        if (interestId == null) {
            throw new IllegalArgumentException("El interés de la actividad es obligatorio");
        }

        activity.setId(null);
        activity.setInterest(interestService.getById(interestId));
        return activityRepository.save(activity);
    }

    // Consultar todas las actividades del catálogo
    @Transactional(readOnly = true)
    public List<ActivityModel> getAll() {
        log.info("Inicia proceso de consultar todas las actividades");
        return activityRepository.findAll();
    }

    // Consultar actividades asociadas a varios intereses
    @Transactional(readOnly = true)
    public List<ActivityModel> getByInterestIds(Set<Long> interestIds) {
        log.info("Consultando actividades para {} intereses", interestIds.size());
        return activityRepository.findByInterestIdIn(interestIds);
    }

    // Consulta actividades por duración, esfuerzo e interés (tipo de actividad)
    @Transactional(readOnly = true)
    public List<ActivityModel> findByFilters(Integer durationMinutes, ActivityEffortEnum effort, Long interestId) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
        if (effort == null) {
            throw new IllegalArgumentException("El esfuerzo es obligatorio");
        }
        if (interestId == null) {
            throw new IllegalArgumentException("El tipo de actividad es obligatorio");
        }

        return activityRepository.findByDurationMinutesAndActivityEffortLevelAndInterestId(
                durationMinutes, effort, interestId);
    }

    // Busca en la BD las actividades elegibles según tiempo, esfuerzo e intereses comunes.
    @Transactional(readOnly = true)
    public List<ActivityModel> findEligibleActivities(
            Integer maxDurationMinutes, ActivityEffortEnum effort, Set<Long> interestIds) {
        if (maxDurationMinutes == null || maxDurationMinutes <= 0) {
            throw new IllegalArgumentException("El tiempo disponible debe ser mayor a 0 minutos");
        }

        boolean hasInterestFilter = interestIds != null && !interestIds.isEmpty();

        if (effort == null && !hasInterestFilter) {
            return activityRepository.findByDurationMinutesLessThanEqual(maxDurationMinutes);
        }
        if (effort == null) {
            return activityRepository.findByDurationMinutesLessThanEqualAndInterestIdIn(
                    maxDurationMinutes, interestIds);
        }
        if (!hasInterestFilter) {
            return activityRepository.findByDurationMinutesLessThanEqualAndActivityEffortLevel(
                    maxDurationMinutes, effort);
        }
        return activityRepository.findByDurationMinutesLessThanEqualAndActivityEffortLevelAndInterestIdIn(
                maxDurationMinutes, effort, interestIds);
    }
}
