package com.backend.gapfinder.entities.userlocationlog;

import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.building.BuildingService;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.gap.GapService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserLocationLogService {

    private final UserLocationLogRepository userLocationLogRepository;
    private final UserService userService;
    private final GapService gapService;
    private final BuildingService buildingService;

    public UserLocationLogService(UserLocationLogRepository userLocationLogRepository,
                                   UserService userService,
                                   GapService gapService,
                                   BuildingService buildingService) {
        this.userLocationLogRepository = userLocationLogRepository;
        this.userService = userService;
        this.gapService = gapService;
        this.buildingService = buildingService;
    }

    // Registrar un checkpoint de ubicación
    @Transactional
    public UserLocationLogEntity create(Long userId, Long gapId, double latitude, double longitude) {
        log.info("Inicia proceso de registrar checkpoint del usuario con id = {} en el gap con id = {}", userId, gapId);

        UserEntity user = userService.getById(userId);
        GapEntity gap = gapService.getById(gapId);

        UserLocationLogEntity checkpoint = new UserLocationLogEntity();
        checkpoint.setUser(user);
        checkpoint.setGap(gap);
        checkpoint.setLatitude(latitude);
        checkpoint.setLongitude(longitude);
        checkpoint.setRecordedAt(LocalDateTime.now());

        // Se resuelve el building automáticamente a partir de las coordenadas
        buildingService.resolveBuildingFromCoordinates(latitude, longitude)
                .ifPresent(checkpoint::setBuilding);

        log.info("Termina proceso de registrar checkpoint del usuario con id = {} en el gap con id = {}", userId, gapId);
        return userLocationLogRepository.save(checkpoint);
    }

    // Consultar los checkpoints de un gap específico
    @Transactional
    public List<UserLocationLogEntity> getAllByGap(Long gapId) {
        log.info("Inicia proceso de consultar los checkpoints del gap con id = {}", gapId);

        gapService.getById(gapId);

        return userLocationLogRepository.findByGapIdOrderByRecordedAtAsc(gapId);
    }

    // Calcular los minutos pasados en cada building durante un gap
    @Transactional
    public Map<BuildingEntity, Integer> calculateTimePerBuilding(Long gapId) {
        log.info("Inicia proceso de calcular tiempo por building del gap con id = {}", gapId);

        GapEntity gap = gapService.getById(gapId);
        List<UserLocationLogEntity> checkpoints = userLocationLogRepository.findByGapIdOrderByRecordedAtAsc(gapId);

        Map<BuildingEntity, Integer> tiempoPorBuilding = new HashMap<>();

        for (int i = 0; i < checkpoints.size(); i++) {
            UserLocationLogEntity actual = checkpoints.get(i);

            if (actual.getBuilding() == null) {
                continue;
            }

            LocalDateTime finDelTramo = (i + 1 < checkpoints.size())
                    ? checkpoints.get(i + 1).getRecordedAt()
                    : gap.getEndTime();

            int minutos = (int) Duration.between(actual.getRecordedAt(), finDelTramo).toMinutes();

            tiempoPorBuilding.merge(actual.getBuilding(), minutos, Integer::sum);
        }

        log.info("Termina proceso de calcular tiempo por building del gap con id = {}", gapId);
        return tiempoPorBuilding;
    }

    // Calcular el building donde más tiempo pasa un usuario en general
    @Transactional
    public Optional<BuildingEntity> calculateFavoriteBuilding(Long userId) {
        log.info("Inicia proceso de calcular building favorito del usuario con id = {}", userId);

        userService.getById(userId);

        List<GapEntity> gaps = gapService.getAllByUser(userId);

        Map<BuildingEntity, Integer> tiempoTotalPorBuilding = new HashMap<>();

        for (GapEntity gap : gaps) {
            Map<BuildingEntity, Integer> tiempoPorGap = calculateTimePerBuilding(gap.getId());
            tiempoPorGap.forEach((building, minutos) ->
                    tiempoTotalPorBuilding.merge(building, minutos, Integer::sum));
        }

        log.info("Termina proceso de calcular building favorito del usuario con id = {}", userId);

        return tiempoTotalPorBuilding.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    // Calcular el building donde más tiempo pasó el usuario dentro de un gap específico
    @Transactional
    public Optional<BuildingEntity> calculateTopBuildingForGap(Long gapId) {
        log.info("Inicia proceso de calcular building con más tiempo en el gap con id = {}", gapId);

        Map<BuildingEntity, Integer> tiempoPorBuilding = calculateTimePerBuilding(gapId);

        log.info("Termina proceso de calcular building con más tiempo en el gap con id = {}", gapId);

        return tiempoPorBuilding.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
}