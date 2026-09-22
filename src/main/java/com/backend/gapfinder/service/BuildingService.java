package com.backend.gapfinder.service;

import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.BuildingModel;
import com.backend.gapfinder.repository.BuildingRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BuildingService {

    // Instancia de GeometryFactory con SRID 4326 (WGS 84 / Estándar GPS)
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    public List<BuildingModel> getAll() {
        log.info("Inicia proceso de consultar todos los edificios");
        return buildingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BuildingModel getById(Long id) {
        log.info("Inicia proceso de consultar el edificio con id = {}", id);
        return buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El edificio con id " + id + " no existe"));
    }

    @Transactional
    public BuildingModel create(BuildingModel building) {
        log.info("Inicia proceso de creación del edificio");

        validateBuildingData(building);

        Optional<BuildingModel> existente = buildingRepository.findByName(building.getName());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un edificio con el nombre " + building.getName());
        }

        building.setId(null);

        log.info("Termina proceso de creación del edificio");
        return buildingRepository.save(building);
    }

    @Transactional
    public BuildingModel update(Long id, BuildingModel building) {
        log.info("Inicia proceso de actualización del edificio con id = {}", id);

        BuildingModel existente = getById(id);
        validateBuildingData(building);

        existente.setName(building.getName());
        existente.setLocation(building.getLocation()); // Se actualiza el objeto Point completo
        existente.setRadiusMeters(building.getRadiusMeters());

        log.info("Termina proceso de actualización del edificio con id = {}", id);
        return buildingRepository.save(existente);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Inicia proceso de eliminación del edificio con id = {}", id);

        BuildingModel existente = getById(id);
        buildingRepository.delete(existente);

        log.info("Termina proceso de eliminación del edificio con id = {}", id);
    }

    // Determina en qué edificio se encuentra el usuario, a partir de sus coordenadas GPS
    @Transactional(readOnly = true)
    public Optional<BuildingModel> findBuildingContainingUser(double latitude, double longitude) {
        log.info("Inicia proceso de resolver edificio a partir de coordenadas ({}, {})", latitude, longitude);

        // Recordar: En JTS Coordinate el orden es (X, Y) -> (longitude, latitude)
        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        // Entre los edificios cuyo radio alcanza el punto del estudiante, se toma el más cercano
        return buildingRepository.findBuildingAtUserLocation(userLocation);
    }

    private void validateBuildingData(BuildingModel building) {
        if (building.getName() == null || building.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (building.getLocation() == null) {
            throw new IllegalArgumentException("La ubicación (Point) es obligatoria");
        }
        if (building.getRadiusMeters() <= 0) {
            throw new IllegalArgumentException("El radio debe ser mayor a 0");
        }
    }
}