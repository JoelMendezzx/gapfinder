package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.ActivityBasicDTO;
import com.backend.gapfinder.dto.response.OpenTableBasicDTO;
import com.backend.gapfinder.dto.response.OpenTableCompleteDTO;
import com.backend.gapfinder.dto.response.OpenTableParticipantBasicDTO;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.model.OpenTableParticipantModel;
import com.backend.gapfinder.service.OpenTableService;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam Integer durationMinutes,
            @RequestBody OpenTableCompleteDTO openTableDTO) {

        OpenTableModel openTableModel = modelMapper.map(openTableDTO, OpenTableModel.class);
        OpenTableModel created = openTableService.create(
                creatorId, buildingId, openTableModel, durationMinutes);

        return modelMapper.map(created, OpenTableCompleteDTO.class);
    }

    // Consultar una Open Table por id
    // GET /open-tables/1
    @GetMapping("/{id}")
    public OpenTableCompleteDTO getOpenTable(
            @PathVariable Long id
    ) {

        OpenTableModel openTable =
                openTableService.getById(id);

        return modelMapper.map(
                openTable,
                OpenTableCompleteDTO.class
        );
    }

        // Todas las Open Tables en las que participa un usuario, activas o terminadas.
        // GET /open-tables/user/1
        @GetMapping("/user/{userId}")
        public List<OpenTableBasicDTO> getAllByUser(@PathVariable Long userId) {
                List<OpenTableModel> openTables = openTableService.getAllByUser(userId);
                return modelMapper.map(openTables, new TypeToken<List<OpenTableBasicDTO>>() {}.getType());
        }

        // Todas las Open Tables creadas por un usuario, activas o terminadas.
        // GET /open-tables/created-by-user/1
        @GetMapping("/created-by-user/{userId}")
        public List<OpenTableBasicDTO> getCreatedByUser(@PathVariable Long userId) {
                List<OpenTableModel> openTables = openTableService.getCreatedByUser(userId);
                return modelMapper.map(openTables, new TypeToken<List<OpenTableBasicDTO>>() {}.getType());
        }

        // Open Tables activas disponibles, excluyendo las creadas o ya ocupadas por el usuario.
        // GET /open-tables/discover?userId=1
        @GetMapping("/discover")
        public List<OpenTableBasicDTO> getDiscoverableForUser(@RequestParam Long userId) {
                List<OpenTableModel> openTables = openTableService.getDiscoverableForUser(userId);
                return modelMapper.map(openTables, new TypeToken<List<OpenTableBasicDTO>>() {}.getType());
        }

    // Listar Open Tables públicas activas de un edificio
    // GET /open-tables/building/2
    @GetMapping("/building/{buildingId}")
    public List<OpenTableBasicDTO> getAllByBuilding(
            @PathVariable Long buildingId
    ) {

        List<OpenTableModel> openTables =
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

        OpenTableParticipantModel participant =
                openTableService.join(
                        id,
                        userId
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

        OpenTableParticipantModel participant =
                openTableService.leave(
                        id,
                        userId
                );

        return modelMapper.map(
                participant,
                OpenTableParticipantBasicDTO.class
        );
    }

    // Sugerir actividades según intereses y duración elegida para la Open Table
    // GET /open-tables/suggested-activities?userId=1&durationMinutes=60
    @GetMapping("/suggested-activities")
    public List<ActivityBasicDTO> getSuggestedActivities(
            @RequestParam Long userId,
            @RequestParam Integer durationMinutes
    ) {

        List<ActivityModel> activities =
                openTableService.calculateSuggestedActivities(
                        userId,
                        durationMinutes
                );

        return modelMapper.map(
                activities,
                new TypeToken<List<ActivityBasicDTO>>() {
                }.getType()
        );
    }
}