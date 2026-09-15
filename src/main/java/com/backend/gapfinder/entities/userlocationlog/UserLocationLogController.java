package com.backend.gapfinder.entities.userlocationlog;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.gapfinder.entities.building.BuildingBasicDTO;
import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.building.BuildingTimeDTO;

@RestController
@RequestMapping("/user-location-logs")
public class UserLocationLogController {

    private final UserLocationLogService userLocationLogService;
    private final ModelMapper modelMapper;

    public UserLocationLogController(
            UserLocationLogService userLocationLogService,
            ModelMapper modelMapper
    ) {
        this.userLocationLogService = userLocationLogService;
        this.modelMapper = modelMapper;
    }

    // Registrar un checkpoint de ubicación de un usuario durante un GAP
    // POST /user-location-logs?userId=1&gapId=5&latitude=4.60&longitude=-74.06
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserLocationLogBasicDTO create(
            @RequestParam Long userId,
            @RequestParam Long gapId,
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {

        UserLocationLogEntity checkpoint =
                userLocationLogService.create(
                        userId,
                        gapId,
                        latitude,
                        longitude
                );

        return modelMapper.map(
                checkpoint,
                UserLocationLogBasicDTO.class
        );
    }

    // Listar los checkpoints de un GAP en orden cronológico
    // GET /user-location-logs/gap/5
    @GetMapping("/gap/{gapId}")
    public List<UserLocationLogBasicDTO> getAllByGap(
            @PathVariable Long gapId
    ) {

        List<UserLocationLogEntity> checkpoints =
                userLocationLogService.getAllByGap(gapId);

        return modelMapper.map(
                checkpoints,
                new TypeToken<List<UserLocationLogBasicDTO>>() {
                }.getType()
        );
    }

    // Calcular cuántos minutos pasó el usuario en cada edificio durante un GAP
    // GET /user-location-logs/gap/5/time-per-building
    @GetMapping("/gap/{gapId}/time-per-building")
    public List<BuildingTimeDTO> getTimePerBuilding(
            @PathVariable Long gapId
    ) {

        Map<BuildingEntity, Integer> tiempoPorBuilding =
                userLocationLogService.calculateTimePerBuilding(gapId);

        return tiempoPorBuilding
                .entrySet()
                .stream()
                .map(entry -> {

                    BuildingTimeDTO dto =
                            new BuildingTimeDTO();

                    dto.setBuilding(
                            modelMapper.map(
                                    entry.getKey(),
                                    BuildingBasicDTO.class
                            )
                    );

                    dto.setMinutes(
                            entry.getValue()
                    );

                    return dto;
                })
                .toList();
    }

    // Consultar el edificio donde más tiempo pasa un usuario en general
    // GET /user-location-logs/user/1/favorite-building
    @GetMapping("/user/{userId}/favorite-building")
    public BuildingBasicDTO getFavoriteBuilding(
            @PathVariable Long userId
    ) {

        Optional<BuildingEntity> favorite =
                userLocationLogService
                        .calculateFavoriteBuilding(userId);

        if (favorite.isEmpty()) {
            return null;
        }

        return modelMapper.map(
                favorite.get(),
                BuildingBasicDTO.class
        );
    }

    // Consultar el edificio donde más tiempo pasó un usuario durante un GAP
    // GET /user-location-logs/gap/5/top-building
    @GetMapping("/gap/{gapId}/top-building")
    public BuildingBasicDTO getTopBuildingForGap(
            @PathVariable Long gapId
    ) {

        Optional<BuildingEntity> topBuilding =
                userLocationLogService
                        .calculateTopBuildingForGap(gapId);

        if (topBuilding.isEmpty()) {
            return null;
        }

        return modelMapper.map(
                topBuilding.get(),
                BuildingBasicDTO.class
        );
    }
}