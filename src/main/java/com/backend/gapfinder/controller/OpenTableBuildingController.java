package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.OpenTableBasicDTO;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.service.OpenTableBuildingService;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open-table-buildings")
public class OpenTableBuildingController {

    private final OpenTableBuildingService openTableBuildingService;
    private final ModelMapper modelMapper;

    public OpenTableBuildingController(OpenTableBuildingService openTableBuildingService, ModelMapper modelMapper) {
        this.openTableBuildingService = openTableBuildingService;
        this.modelMapper = modelMapper;
    }

    // Obtiene las Open Tables activas de un edificio
    // GET /open-table-buildings/building/{buildingId}
    @GetMapping("/building/{buildingId}")
    public List<OpenTableBasicDTO> getByBuilding(@PathVariable Long buildingId) {
        List<OpenTableModel> openTables = openTableBuildingService.getAllByBuilding(buildingId);
        return modelMapper.map(openTables, new TypeToken<List<OpenTableBasicDTO>>() {}.getType());
    }

    // Sugiere Open Tables activas del edificio favorito del usuario que caben en su GAP actual
    // (lista vacía si no tiene GAPs hoy, no tiene edificio favorito o no hay mesas que encajen)
    // GET /open-table-buildings/user/{userId}/suggested
    @GetMapping("/user/{userId}/suggested")
    public List<OpenTableBasicDTO> getSuggestedForUser(@PathVariable Long userId) {
        List<OpenTableModel> suggested = openTableBuildingService.getSuggestedForUser(userId);
        return modelMapper.map(suggested, new TypeToken<List<OpenTableBasicDTO>>() {}.getType());
    }
}