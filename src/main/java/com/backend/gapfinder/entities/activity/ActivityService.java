package com.backend.gapfinder.entities.activity;

import com.backend.gapfinder.entities.interest.InterestService;
import com.backend.gapfinder.exceptions.NotFoundException;
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
    public ActivityEntity getById(Long id) {
        log.info("Inicia proceso de consultar la actividad con id = {}", id);
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La actividad con id " + id + " no existe"));
    }

    // Crear una actividad asociada a un interés existente
    @Transactional
    public ActivityEntity create(ActivityEntity activity, Long interestId) {
        if (activity == null || activity.getTitle() == null || activity.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título de la actividad es obligatorio");
        }
        if (activity.getDurationMinutes() == null || activity.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("La duración de la actividad debe ser mayor a 0 minutos");
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
    public List<ActivityEntity> getAll() {
        log.info("Inicia proceso de consultar todas las actividades");
        return activityRepository.findAll();
    }

    // Consultar actividades asociadas a varios intereses
    @Transactional(readOnly = true)
    public List<ActivityEntity> getByInterestIds(Set<Long> interestIds) {
        log.info("Consultando actividades para {} intereses", interestIds.size());
        return activityRepository.findByInterestIdIn(interestIds);
    }
}
