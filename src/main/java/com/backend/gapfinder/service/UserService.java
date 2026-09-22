package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<UserModel> getAll() {
        log.info("Inicia proceso de consultar todos los usuarios");
        return userRepository.findAll();
    }

    // Consultar un usuario por id
    @Transactional
    public UserModel getById(Long id) {
        log.info("Inicia proceso de consultar el usuario con id = {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id " + id + " no existe"));
    }

    // Buscar un usuario por email
    @Transactional
    public UserModel findByEmail(String email) {
        log.info("Inicia proceso de buscar usuario por email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No existe un usuario con el email " + email));
    }

    // Buscar usuarios por nombre
    @Transactional(readOnly = true)
    public List<UserModel> findByName(String name) {
        log.info("Inicia proceso de buscar usuarios por nombre");
        return userRepository.findByName(name);
    }

    // Crear un usuario
    @Transactional
    public UserModel create(UserModel user) {
        log.info("Inicia proceso de creación del usuario");

        validateUserData(user);

        Optional<UserModel> existente = userRepository.findByEmail(user.getEmail());
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
    public UserModel update(Long id, UserModel user) {
        log.info("Inicia proceso de actualización del usuario con id = {}", id);

        UserModel existente = getById(id);
        validateUserData(user);

        existente.setName(user.getName());
        existente.setProgram(user.getProgram());
        existente.setSemester(user.getSemester());
        existente.setAvatarUrl(user.getAvatarUrl());
        existente.setActivityEffortPreference(user.getActivityEffortPreference());

        log.info("Termina proceso de actualización del usuario con id = {}", id);
        return userRepository.save(existente);
    }

    // Actualizar la ubicación actual de un usuario a partir de sus coordenadas GPS,
    // sin depender de que tenga un GAP activo
    @Transactional
    public UserModel updateCurrentLocation(Long userId, double latitude, double longitude) {
        log.info("Inicia proceso de actualizar ubicación actual del usuario con id = {}", userId);

        UserModel user = getById(userId);

        buildingService.findBuildingContainingUser(latitude, longitude)
                .ifPresentOrElse(
                        user::setCurrentBuilding,
                        () -> user.setCurrentBuilding(null)
                );
        user.setLocationUpdatedAt(LocalDateTime.now());

        log.info("Termina proceso de actualizar ubicación actual del usuario con id = {}", userId);
        return userRepository.save(user);
    }

    // Eliminar usuario: fuera de alcance del proyecto (decisión ya tomada).
    // Descomentar solo si se decide reactivar esta función.
    /*
    @Transactional
    public void delete(Long id) {
        UserModel user = getById(id);
        userRepository.delete(user);
    }
    */

    // Validar los datos de un usuario
    private void validateUserData(UserModel user) {
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
    public UserModel addInterest(Long userId, Long interestId) {
        log.info("Agregando interés {} al usuario {}", interestId, userId);
        UserModel user = getById(userId);
        InterestModel interest = interestService.getById(interestId);

        if (!user.getInterests().contains(interest)) {
            user.getInterests().add(interest);
        }

        return userRepository.save(user);
    }

    // Quitar un interés de un usuario
    @Transactional
    public UserModel removeInterest(Long userId, Long interestId) {
        log.info("Quitando interés {} del usuario {}", interestId, userId);
        UserModel user = getById(userId);
        user.getInterests().removeIf(i -> i.getId().equals(interestId));
        return userRepository.save(user);
    }

    // Consultar todos los intereses de un usuario
    @Transactional(readOnly = true)
    public List<InterestModel> getInterests(Long userId) {
        log.info("Consultando los intereses del usuario con id = {}", userId);
        return new ArrayList<>(getById(userId).getInterests());
    }

    // Actualizar la preferencia de esfuerzo de un usuario
    @Transactional
    public UserModel updateEffortPreference(Long userId, ActivityEffortEnum effort) {
        log.info("Actualizando preferencia de esfuerzo del usuario con id = {}", userId);

        if (effort == null) {
            throw new IllegalArgumentException("La preferencia de esfuerzo es obligatoria");
        }

        UserModel user = getById(userId);
        user.setActivityEffortPreference(effort);

        return userRepository.save(user);
    }
}