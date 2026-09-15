package com.backend.gapfinder.entities.opentable;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.gapfinder.entities.activity.ActivityBasicDTO;
import com.backend.gapfinder.entities.activity.ActivityEntity;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantBasicDTO;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantEntity;

@RestController
@RequestMapping("/open-tables")
public class OpenTableController {

    private final OpenTableService openTableService;
    private final ModelMapper modelMapper;

    public OpenTableController(
            OpenTableService openTableService,
            ModelMapper modelMapper
    ) {
        this.openTableService = openTableService;
        this.modelMapper = modelMapper;
    }

    // Crear una Open Table
    // POST /open-tables?creatorId=1&buildingId=2&durationMinutes=60
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpenTableCompleteDTO createOpenTable(
            @RequestParam Long creatorId, @RequestParam Long buildingId,
            @RequestParam(required = false) Long groupId, @RequestParam Integer durationMinutes,
            @RequestBody OpenTableCompleteDTO openTableDTO) {

        OpenTableEntity openTableEntity = modelMapper.map(openTableDTO, OpenTableEntity.class);
        OpenTableEntity created = openTableService.create(
                creatorId, buildingId, groupId, openTableEntity, durationMinutes);

        return modelMapper.map(created, OpenTableCompleteDTO.class);
    }

    // Consultar una Open Table por id
    // GET /open-tables/1
    @GetMapping("/{id}")
    public OpenTableCompleteDTO getOpenTable(
            @PathVariable Long id
    ) {

        OpenTableEntity openTable =
                openTableService.getById(id);

        return modelMapper.map(
                openTable,
                OpenTableCompleteDTO.class
        );
    }

    // Listar Open Tables públicas activas de un edificio
    // GET /open-tables/building/2
    @GetMapping("/building/{buildingId}")
    public List<OpenTableBasicDTO> getAllByBuilding(
            @PathVariable Long buildingId
    ) {

        List<OpenTableEntity> openTables =
                openTableService.getAllByBuilding(buildingId);

        return modelMapper.map(
                openTables,
                new TypeToken<List<OpenTableBasicDTO>>() {
                }.getType()
        );
    }

    // Consultar cantidad de Open Tables activas por edificio
    // GET /open-tables/active-count
    @GetMapping("/active-count")
    public Map<String, Long> getActiveOnes() {

        return openTableService.getActiveOnes();
    }

    // Unirse a una Open Table pública
    // POST /open-tables/1/join?userId=5
    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.CREATED)
    public OpenTableParticipantBasicDTO join(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {

        OpenTableParticipantEntity participant =
                openTableService.join(
                        id,
                        userId
                );

        return modelMapper.map(
                participant,
                OpenTableParticipantBasicDTO.class
        );
    }

    // Responder una invitación de una Open Table privada
    // PATCH /open-tables/1/respond?userId=5&accept=true
    @PatchMapping("/{id}/respond")
    public OpenTableParticipantBasicDTO respondToInvite(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam boolean accept
    ) {

        OpenTableParticipantEntity participant =
                openTableService.respondToInvite(
                        id,
                        userId,
                        accept
                );

        return modelMapper.map(
                participant,
                OpenTableParticipantBasicDTO.class
        );
    }

    // Salir de una Open Table
    // PATCH /open-tables/1/leave?userId=5
    @PatchMapping("/{id}/leave")
    public OpenTableParticipantBasicDTO leave(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {

        OpenTableParticipantEntity participant =
                openTableService.leave(
                        id,
                        userId
                );

        return modelMapper.map(
                participant,
                OpenTableParticipantBasicDTO.class
        );
    }

    // Sugerir actividades según intereses y duración disponible del GAP
    // GET /open-tables/suggested-activities?userId=1&gapDurationMinutes=60
    @GetMapping("/suggested-activities")
    public List<ActivityBasicDTO> getSuggestedActivities(
            @RequestParam Long userId,
            @RequestParam Integer gapDurationMinutes
    ) {

        List<ActivityEntity> activities =
                openTableService.calculateSuggestedActivities(
                        userId,
                        gapDurationMinutes
                );

        return modelMapper.map(
                activities,
                new TypeToken<List<ActivityBasicDTO>>() {
                }.getType()
        );
    }
}