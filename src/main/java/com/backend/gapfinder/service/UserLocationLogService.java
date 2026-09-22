package com.backend.gapfinder.service;

import com.backend.gapfinder.model.BuildingModel;
import com.backend.gapfinder.model.GapModel;
import com.backend.gapfinder.model.UserLocationLogModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.UserLocationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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

    // Instancia para crear objetos Point en coordenadas WGS 84 (SRID 4326)
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

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

    // Registrar un checkpoint de ubicación (solo si el GAP sigue activo)
    @Transactional
    public UserLocationLogModel create(Long userId, Long gapId, double latitude, double longitude) {
        log.info("Inicia proceso de registrar checkpoint del usuario con id = {} en el gap con id = {}", userId, gapId);

        UserModel user = userService.getById(userId);
        GapModel gap = gapService.getById(gapId);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(gap.getStartTime()) || now.isAfter(gap.getEndTime())) {
            throw new IllegalStateException("El GAP con id " + gapId + " no está activo en este momento");
        }

        // Crear objeto Point (Recordar: JTS usa el orden X, Y -> Longitud, Latitud)
        Point locationPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        UserLocationLogModel checkpoint = new UserLocationLogModel();
        checkpoint.setUser(user);
        checkpoint.setGap(gap);
        checkpoint.setLocation(locationPoint);
        checkpoint.setRecordedAt(now);

        // Se resuelve el building automáticamente a partir de las coordenadas
        buildingService.findBuildingContainingUser(latitude, longitude)
                .ifPresent(checkpoint::setBuilding);

        log.info("Termina proceso de registrar checkpoint del usuario con id = {} en el gap con id = {}", userId, gapId);
        return userLocationLogRepository.save(checkpoint);
    }

    // Consultar los checkpoints de un gap específico
    @Transactional(readOnly = true)
    public List<UserLocationLogModel> getAllByGap(Long gapId) {
        log.info("Inicia proceso de consultar los checkpoints del gap con id = {}", gapId);

        gapService.getById(gapId);

        return userLocationLogRepository.findByGapIdOrderByRecordedAtAsc(gapId);
    }

    // Calcular los minutos pasados en cada building durante un gap
    @Transactional(readOnly = true)
    public Map<BuildingModel, Integer> calculateTimePerBuilding(Long gapId) {
        log.info("Inicia proceso de calcular tiempo por building del gap con id = {}", gapId);

        GapModel gap = gapService.getById(gapId);
        List<UserLocationLogModel> checkpoints = userLocationLogRepository.findByGapIdOrderByRecordedAtAsc(gapId);

        Map<BuildingModel, Integer> tiempoPorBuilding = new HashMap<>();

        for (int i = 0; i < checkpoints.size(); i++) {
            UserLocationLogModel actual = checkpoints.get(i);

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
    @Transactional(readOnly = true)
    public Optional<BuildingModel> calculateFavoriteBuilding(Long userId) {
        log.info("Inicia proceso de calcular building favorito del usuario con id = {}", userId);

        userService.getById(userId);

        Optional<BuildingModel> favorito = userLocationLogRepository.findFavoriteBuildingForUser(userId)
                .map(proyeccion -> buildingService.getById(proyeccion.getBuildingId()));

        log.info("Termina proceso de calcular building favorito del usuario con id = {}", userId);
        return favorito;
    }

    // Calcular el building donde más tiempo pasó el usuario dentro de un gap específico
    @Transactional(readOnly = true)
    public Optional<BuildingModel> calculateTopBuildingForGap(Long gapId) {
        log.info("Inicia proceso de calcular building con más tiempo en el gap con id = {}", gapId);

        Map<BuildingModel, Integer> tiempoPorBuilding = calculateTimePerBuilding(gapId);

        log.info("Termina proceso de calcular building con más tiempo en el gap con id = {}", gapId);

        return tiempoPorBuilding.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
}