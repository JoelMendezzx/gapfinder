package com.backend.gapfinder.entities.user;

import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.building.BuildingService;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.interest.InterestService;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BuildingService buildingService;
    private final InterestService interestService;

    public UserService(UserRepository userRepository, BuildingService buildingService,
                       InterestService interestService) {
        this.userRepository = userRepository;
        this.buildingService = buildingService;
        this.interestService = interestService;
    }

    // Consultar todos los usuarios
    @Transactional
    public List<UserEntity> getAll() {
        log.info("Inicia proceso de consultar todos los usuarios");
        return userRepository.findAll();
    }

    // Consultar un usuario por id
    @Transactional
    public UserEntity getById(Long id) {
        log.info("Inicia proceso de consultar el usuario con id = {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id " + id + " no existe"));
    }

    // Buscar un usuario por email
    @Transactional
    public UserEntity findByEmail(String email) {
        log.info("Inicia proceso de buscar usuario por email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No existe un usuario con el email " + email));
    }

    // Crear un usuario
    @Transactional
    public UserEntity create(UserEntity user) {
        log.info("Inicia proceso de creación del usuario");

        validateUserData(user);

        Optional<UserEntity> existente = userRepository.findByEmail(user.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email " + user.getEmail());
        }

        user.setId(null);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        log.info("Termina proceso de creación del usuario");
        return userRepository.save(user);
    }

    // Actualizar los datos de un usuario
    @Transactional
    public UserEntity update(Long id, UserEntity user) {
        log.info("Inicia proceso de actualización del usuario con id = {}", id);

        UserEntity existente = getById(id);
        validateUserData(user);

        existente.setName(user.getName());
        existente.setProgram(user.getProgram());
        existente.setSemester(user.getSemester());
        existente.setAvatarUrl(user.getAvatarUrl());
        existente.setActivityEffortPreference(user.getActivityEffortPreference());

        log.info("Termina proceso de actualización del usuario con id = {}", id);
        return userRepository.save(existente);
    }

    // Actualizar la ubicación actual de un usuario
    @Transactional
    public UserEntity updateCurrentLocation(Long userId, Long buildingId) {
        log.info("Inicia proceso de actualizar ubicación actual del usuario con id = {}", userId);

        UserEntity user = getById(userId);
        BuildingEntity building = buildingService.getById(buildingId);

        user.setCurrentBuilding(building);
        user.setLocationUpdatedAt(LocalDateTime.now());

        log.info("Termina proceso de actualizar ubicación actual del usuario con id = {}", userId);
        return userRepository.save(user);
    }

    // Eliminar usuario: fuera de alcance del proyecto (decisión ya tomada).
    // Descomentar solo si se decide reactivar esta función.
    /*
    @Transactional
    public void delete(Long id) {
        UserEntity user = getById(id);
        userRepository.delete(user);
    }
    */

    // Validar los datos de un usuario
    private void validateUserData(UserEntity user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
    }

    // Agregar un interés a un usuario
    @Transactional
    public UserEntity addInterest(Long userId, Long interestId) {
        log.info("Agregando interés {} al usuario {}", interestId, userId);
        UserEntity user = getById(userId);
        InterestEntity interest = interestService.getById(interestId);

        if (!user.getInterests().contains(interest)) {
            user.getInterests().add(interest);
        }

        return userRepository.save(user);
    }

    // Quitar un interés de un usuario
    @Transactional
    public UserEntity removeInterest(Long userId, Long interestId) {
        log.info("Quitando interés {} del usuario {}", interestId, userId);
        UserEntity user = getById(userId);
        user.getInterests().removeIf(i -> i.getId().equals(interestId));
        return userRepository.save(user);
    }
}