package com.backend.gapfinder.entities.interest;

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
        List<InterestEntity> interests = interestService.getAll();
        return modelMapper.map(interests, new TypeToken<List<InterestBasicDTO>>() {}.getType());
    }

    // Obtiene un interés dado su id
    // GET /interests/{id}
    @GetMapping("/{id}")
    public InterestBasicDTO getInterest(@PathVariable Long id) {
        InterestEntity interest = interestService.getById(id);
        return modelMapper.map(interest, InterestBasicDTO.class);
    }

    // Crea un nuevo interés
    // POST /interests
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestBasicDTO createInterest(@RequestBody InterestBasicDTO interestDTO) {
        InterestEntity interestEntity = modelMapper.map(interestDTO, InterestEntity.class);
        InterestEntity created = interestService.create(interestEntity);
        return modelMapper.map(created, InterestBasicDTO.class);
    }

    // Actualiza un interés existente
    // PUT /interests/{id}
    @PutMapping("/{id}")
    public InterestBasicDTO updateInterest(@PathVariable Long id, @RequestBody InterestBasicDTO interestDTO) {
        InterestEntity interestEntity = modelMapper.map(interestDTO, InterestEntity.class);
        InterestEntity updated = interestService.update(id, interestEntity);
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
