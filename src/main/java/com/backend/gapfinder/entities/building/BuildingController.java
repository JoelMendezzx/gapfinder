package com.backend.gapfinder.entities.building;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final ModelMapper modelMapper;

    public BuildingController(BuildingService buildingService, ModelMapper modelMapper) {
        this.buildingService = buildingService;
        this.modelMapper = modelMapper;
    }

    // Obtiene todos los edificios
    // GET /buildings
    @GetMapping
    public List<BuildingBasicDTO> getBuildings() {
        List<BuildingEntity> buildings = buildingService.getAll();
        return modelMapper.map(buildings, new TypeToken<List<BuildingBasicDTO>>() {}.getType());
    }

    // Obtiene un edificio dado su id
    // GET /buildings/{id}
    @GetMapping("/{id}")
    public BuildingBasicDTO getBuilding(@PathVariable Long id) {
        BuildingEntity building = buildingService.getById(id);
        return modelMapper.map(building, BuildingBasicDTO.class);
    }

    // Resuelve a qué edificio pertenecen unas coordenadas
    // GET /buildings/resolve?latitude=4.60&longitude=-74.06
    @GetMapping("/resolve")
    public BuildingBasicDTO resolveFromCoordinates(@RequestParam double latitude, @RequestParam double longitude) {
        Optional<BuildingEntity> building = buildingService.resolveBuildingFromCoordinates(latitude, longitude);
        return building.map(b -> modelMapper.map(b, BuildingBasicDTO.class)).orElse(null);
    }

    // Crea un nuevo edificio
    // POST /buildings
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingBasicDTO createBuilding(@RequestBody BuildingBasicDTO buildingDTO) {
        BuildingEntity buildingEntity = modelMapper.map(buildingDTO, BuildingEntity.class);
        BuildingEntity created = buildingService.create(buildingEntity);
        return modelMapper.map(created, BuildingBasicDTO.class);
    }

    // Actualiza un edificio existente
    // PUT /buildings/{id}
    @PutMapping("/{id}")
    public BuildingBasicDTO updateBuilding(@PathVariable Long id, @RequestBody BuildingBasicDTO buildingDTO) {
        BuildingEntity buildingEntity = modelMapper.map(buildingDTO, BuildingEntity.class);
        BuildingEntity updated = buildingService.update(id, buildingEntity);
        return modelMapper.map(updated, BuildingBasicDTO.class);
    }

    // Elimina un edificio
    // DELETE /buildings/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBuilding(@PathVariable Long id) {
        buildingService.delete(id);
    }

}