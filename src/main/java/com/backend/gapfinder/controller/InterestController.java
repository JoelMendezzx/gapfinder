package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.InterestBasicDTO;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.service.InterestService;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interests")
public class InterestController {

    private final InterestService interestService;
    private final ModelMapper modelMapper;

    public InterestController(InterestService interestService, ModelMapper modelMapper) {
        this.interestService = interestService;
        this.modelMapper = modelMapper;
    }

    // Obtiene todos los intereses
    // GET /interests
    @GetMapping
    public List<InterestBasicDTO> getInterests() {
        List<InterestModel> interests = interestService.getAll();
        return modelMapper.map(interests, new TypeToken<List<InterestBasicDTO>>() {}.getType());
    }

    // Obtiene un interés dado su id
    // GET /interests/{id}
    @GetMapping("/{id}")
    public InterestBasicDTO getInterest(@PathVariable Long id) {
        InterestModel interest = interestService.getById(id);
        return modelMapper.map(interest, InterestBasicDTO.class);
    }

    // Crea un nuevo interés
    // POST /interests
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestBasicDTO createInterest(@RequestBody InterestBasicDTO interestDTO) {
        InterestModel interestModel = modelMapper.map(interestDTO, InterestModel.class);
        InterestModel created = interestService.create(interestModel);
        return modelMapper.map(created, InterestBasicDTO.class);
    }

    // Actualiza un interés existente
    // PUT /interests/{id}
    @PutMapping("/{id}")
    public InterestBasicDTO updateInterest(@PathVariable Long id, @RequestBody InterestBasicDTO interestDTO) {
        InterestModel interestModel = modelMapper.map(interestDTO, InterestModel.class);
        InterestModel updated = interestService.update(id, interestModel);
        return modelMapper.map(updated, InterestBasicDTO.class);
    }

    // Elimina un interés
    // DELETE /interests/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterest(@PathVariable Long id) {
        interestService.delete(id);
    }
}
