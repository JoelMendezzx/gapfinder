package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.VisibilitySettingsBasicDTO;
import com.backend.gapfinder.model.VisibilitySettingsModel;
import com.backend.gapfinder.service.VisibilitySettingsService;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visibility-settings")
public class VisibilitySettingsController {

    private final VisibilitySettingsService visibilitySettingsService;
    private final ModelMapper modelMapper;

    public VisibilitySettingsController(VisibilitySettingsService visibilitySettingsService, ModelMapper modelMapper) {
        this.visibilitySettingsService = visibilitySettingsService;
        this.modelMapper = modelMapper;
    }

    // Crea la configuración de visibilidad de un usuario
    // POST /visibility-settings?userId=1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisibilitySettingsBasicDTO createVisibilitySettings(
            @RequestParam Long userId,
            @RequestBody VisibilitySettingsBasicDTO dto) {

        VisibilitySettingsModel entity = modelMapper.map(dto, VisibilitySettingsModel.class);
        VisibilitySettingsModel created = visibilitySettingsService.create(userId, entity);
        return modelMapper.map(created, VisibilitySettingsBasicDTO.class);
    }

    // Obtiene la configuración de visibilidad de un usuario
    // GET /visibility-settings/user/{userId}
    @GetMapping("/user/{userId}")
    public VisibilitySettingsBasicDTO getByUser(@PathVariable Long userId) {
        VisibilitySettingsModel entity = visibilitySettingsService.getByUser(userId);
        return modelMapper.map(entity, VisibilitySettingsBasicDTO.class);
    }

    // Actualiza la configuración de visibilidad de un usuario
    // PUT /visibility-settings/user/{userId}
    @PutMapping("/user/{userId}")
    public VisibilitySettingsBasicDTO updateVisibilitySettings(
            @PathVariable Long userId,
            @RequestBody VisibilitySettingsBasicDTO dto) {

        VisibilitySettingsModel entity = modelMapper.map(dto, VisibilitySettingsModel.class);
        VisibilitySettingsModel updated = visibilitySettingsService.update(userId, entity);
        return modelMapper.map(updated, VisibilitySettingsBasicDTO.class);
    }
}
