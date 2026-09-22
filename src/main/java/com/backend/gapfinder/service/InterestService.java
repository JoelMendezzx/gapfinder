
package com.backend.gapfinder.service;

import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.repository.InterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InterestService {

    private final InterestRepository interestRepository;

    public InterestService(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    // Consultar todos los intereses
    @Transactional
    public List<InterestModel> getAll() {
        log.info("Inicia proceso de consultar todos los intereses");
        return interestRepository.findAll();
    }

    // Consultar un interés por id
    @Transactional
    public InterestModel getById(Long id) {
        log.info("Inicia proceso de consultar el interés con id = {}", id);
        return interestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El interés con id " + id + " no existe"));
    }

    // Crear un nuevo interés
    @Transactional
    public InterestModel create(InterestModel interest) {
        log.info("Inicia proceso de creación del interés");

        validateInterestData(interest);

        Optional<InterestModel> existente = interestRepository.findByName(interest.getName());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un interés con el nombre " + interest.getName());
        }

        interest.setId(null);

        log.info("Termina proceso de creación del interés");
        return interestRepository.save(interest);
    }

    // Editar el nombre de un interés existente
    @Transactional
    public InterestModel update(Long id, InterestModel interest) {
        log.info("Inicia proceso de actualización del interés con id = {}", id);

        InterestModel existente = getById(id);
        validateInterestData(interest);

        existente.setName(interest.getName());

        log.info("Termina proceso de actualización del interés con id = {}", id);
        return interestRepository.save(existente);
    }

    // Eliminar un interés existente
    @Transactional
    public void delete(Long id) {
        log.info("Inicia proceso de eliminación del interés con id = {}", id);

        InterestModel existente = getById(id);
        interestRepository.delete(existente);

        log.info("Termina proceso de eliminación del interés con id = {}", id);
    }

    // Validar que los datos del interés sean correctos
    private void validateInterestData(InterestModel interest) {
        if (interest.getName() == null || interest.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}