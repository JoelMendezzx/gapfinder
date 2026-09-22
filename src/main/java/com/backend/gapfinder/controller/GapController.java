package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.GapBasicDTO;
import com.backend.gapfinder.dto.response.GapCompleteDTO;
import com.backend.gapfinder.dto.response.UserBasicDTO;
import com.backend.gapfinder.model.GapModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.service.GapService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gaps")
public class GapController {

    private final GapService gapService;
    private final ModelMapper modelMapper;

    public GapController(GapService gapService, ModelMapper modelMapper) {
        this.gapService = gapService;
        this.modelMapper = modelMapper;
    }

    // Obtiene un GAP dado su id
    // GET /gaps/{id}
    @GetMapping("/{id}")
    public GapCompleteDTO getGap(@PathVariable Long id) {
        GapModel gap = gapService.getById(id);
        return modelMapper.map(gap, GapCompleteDTO.class);
    }

    // Obtiene el historial completo de GAPs de un usuario
    // GET /gaps/user/{userId}
    @GetMapping("/user/{userId}")
    public List<GapCompleteDTO> getAllByUser(@PathVariable Long userId) {
        List<GapModel> gaps = gapService.getAllByUser(userId);
        return modelMapper.map(gaps, new TypeToken<List<GapCompleteDTO>>() {}.getType());
    }

    // Obtiene los GAPs de hoy de un usuario
    // GET /gaps/user/{userId}/today
    @GetMapping("/user/{userId}/today")
    public List<GapCompleteDTO> getTodayByUser(@PathVariable Long userId) {
        List<GapModel> gaps = gapService.getTodayByUser(userId);
        return modelMapper.map(gaps, new TypeToken<List<GapCompleteDTO>>() {}.getType());
    }

    // Crea un GAP manualmente para un usuario
    // POST /gaps/user/{userId}
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GapCompleteDTO createGap(@PathVariable Long userId, @RequestBody GapBasicDTO dto) {
        GapModel gapModel = modelMapper.map(dto, GapModel.class);
        GapModel created = gapService.create(userId, gapModel);
        return modelMapper.map(created, GapCompleteDTO.class);
    }

    // Calcula (y guarda) los GAPs de un usuario para una fecha, a partir de su horario de clases
    // POST /gaps/user/{userId}/calculate?date=2026-09-14
    @PostMapping("/user/{userId}/calculate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<GapCompleteDTO> calculateGapsFromSchedule(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GapModel> gaps = gapService.calculateGapsFromSchedule(userId, date);
        return modelMapper.map(gaps, new TypeToken<List<GapCompleteDTO>>() {}.getType());
    }

    // Obtiene el GAP activo de un usuario en un rango de tiempo (si existe)
    // GET /gaps/user/{userId}/active?startTime=2026-09-14T09:30:00&endTime=2026-09-14T11:00:00
    @GetMapping("/user/{userId}/active")
    public GapCompleteDTO getActiveGap(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        Optional<GapModel> gap = gapService.getActiveGap(userId, startTime, endTime);

        if (gap.isEmpty()) {
            return null;
        }

        return modelMapper.map(gap.get(), GapCompleteDTO.class);
    }

    // Obtiene qué amigos (de una lista de ids) están libres ahora mismo
    // GET /gaps/available-friends?friendIds=1,2,3
    @GetMapping("/available-friends")
    public List<UserBasicDTO> getAvailableFriendsNow(@RequestParam List<Long> friendIds) {
        List<UserModel> available = gapService.getAvailableFriendsNow(friendIds);
        return modelMapper.map(available, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }

}