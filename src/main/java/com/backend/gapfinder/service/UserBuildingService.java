package com.backend.gapfinder.service;

import com.backend.gapfinder.model.BuildingModel;
import com.backend.gapfinder.repository.UserLocationLogRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserBuildingService {

    private final UserService userService;
    private final BuildingService buildingService;
    private final UserLocationLogRepository userLocationLogRepository;

    public UserBuildingService(UserService userService, 
                               BuildingService buildingService, 
                               UserLocationLogRepository userLocationLogRepository) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.userLocationLogRepository = userLocationLogRepository;
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
}