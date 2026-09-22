package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.model.BuildingModel;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.repository.OpenTableRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OpenTableBuildingService {

    private final BuildingService buildingService;
    private final OpenTableRepository openTableRepository;
    private final UserBuildingService userBuildingService;
    private final GapService gapService;

    public OpenTableBuildingService(BuildingService buildingService,
                                    OpenTableRepository openTableRepository,
                                    UserBuildingService userBuildingService,
                                    GapService gapService) {
        this.buildingService = buildingService;
        this.openTableRepository = openTableRepository;
        this.userBuildingService = userBuildingService;
        this.gapService = gapService;
    }

    // Listar las Open Tables públicas activas de un edificio,
    // mostrando primero las que tienen más tiempo restante
    @Transactional(readOnly = true)
    public List<OpenTableModel> getAllByBuilding(Long buildingId) {
        log.info("Consultando Open Tables del edificio con id = {}", buildingId);

        buildingService.getById(buildingId);

        return openTableRepository.findByBuildingIdAndStatusAndEndTimeAfterOrderByEndTimeDesc(
                buildingId,
                OpenTableStatusEnum.ACTIVE,
                LocalDateTime.now()
        );
    }

    // Sugerir Open Tables activas en el building favorito del usuario que caben en su GAP actual
    @Transactional(readOnly = true)
    public List<OpenTableModel> getSuggestedForUser(Long userId) {
        log.info("Inicia proceso de sugerir open tables para el usuario {}", userId);

        // Sin GAPs hoy no hay nada que sugerir
        if (gapService.getTodayByUser(userId).isEmpty()) {
            return List.of();
        }

        // Building favorito (sin datos aún = sin sugerencias)
        Optional<BuildingModel> favorito = userBuildingService.calculateFavoriteBuilding(userId);
        if (favorito.isEmpty()) {
            return List.of();
        }

        Long favoritoId = favorito.get().getId();
        LocalDateTime ahora = LocalDateTime.now();

        // Open Tables descubribles (activas, no creadas por el usuario y a las que no está unido),
        // solo las del building favorito y que caben en su GAP
        List<OpenTableModel> sugeridas = openTableRepository
                .findDiscoverableForUser(userId, OpenTableStatusEnum.ACTIVE, ResponseStatusEnum.IN, ahora)
                .stream()
                .filter(ot -> ot.getBuilding().getId().equals(favoritoId))
                .filter(ot -> gapService.getActiveGap(userId, ahora, ot.getEndTime()).isPresent())
                .toList();

        log.info("Termina proceso de sugerir open tables para el usuario {}", userId);
        return sugeridas;
    }
}