package com.backend.gapfinder.entities.building;

import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BuildingService {

    // Radio de la Tierra en metros, usado para calcular distancias
    private static final double EARTH_RADIUS_METERS = 6371000;

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    // Consultar todos los edificios
    @Transactional
    public List<BuildingEntity> getAll() {
        log.info("Inicia proceso de consultar todos los edificios");
        return buildingRepository.findAll();
    }

    // Consultar un edificio por id
    @Transactional
    public BuildingEntity getById(Long id) {
        log.info("Inicia proceso de consultar el edificio con id = {}", id);
        return buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El edificio con id " + id + " no existe"));
    }

    // Crear un nuevo edificio
    @Transactional
    public BuildingEntity create(BuildingEntity building) {
        log.info("Inicia proceso de creación del edificio");

        validateBuildingData(building);

        Optional<BuildingEntity> existente = buildingRepository.findByName(building.getName());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un edificio con el nombre " + building.getName());
        }

        building.setId(null);

        log.info("Termina proceso de creación del edificio");
        return buildingRepository.save(building);
    }

    // Editar los datos de un edificio existente
    @Transactional
    public BuildingEntity update(Long id, BuildingEntity building) {
        log.info("Inicia proceso de actualización del edificio con id = {}", id);

        BuildingEntity existente = getById(id);
        validateBuildingData(building);

        existente.setName(building.getName());
        existente.setLatitude(building.getLatitude());
        existente.setLongitude(building.getLongitude());
        existente.setRadiusMeters(building.getRadiusMeters());

        log.info("Termina proceso de actualización del edificio con id = {}", id);
        return buildingRepository.save(existente);
    }

    // Eliminar un edificio existente
    @Transactional
    public void delete(Long id) {
        log.info("Inicia proceso de eliminación del edificio con id = {}", id);

        BuildingEntity existente = getById(id);
        buildingRepository.delete(existente);

        log.info("Termina proceso de eliminación del edificio con id = {}", id);
    }

    // Dado un lat/lng, determinar en qué edificio cae (según su radio)
    @Transactional
    public Optional<BuildingEntity> resolveBuildingFromCoordinates(double latitude, double longitude) {
        log.info("Inicia proceso de resolver edificio a partir de coordenadas ({}, {})", latitude, longitude);

        return buildingRepository.findAll().stream()
                .filter(building -> isWithinRadius(building, latitude, longitude))
                .findFirst();
    }

    // Calcular si un punto cae dentro del radio de un edificio
    private boolean isWithinRadius(BuildingEntity building, double latitude, double longitude) {
        double distance = haversineDistance(
                building.getLatitude(), building.getLongitude(),
                latitude, longitude
        );
        return distance <= building.getRadiusMeters();
    }

    // Calcular la distancia en metros entre dos coordenadas (fórmula de Haversine)
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    // Validar que los datos del edificio sean correctos
    private void validateBuildingData(BuildingEntity building) {
        if (building.getName() == null || building.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (building.getRadiusMeters() <= 0) {
            throw new IllegalArgumentException("El radio debe ser mayor a 0");
        }
    }
}