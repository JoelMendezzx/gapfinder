package com.backend.gapfinder.entities.building;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Long> {

    Optional<BuildingEntity> findByName(String name);
}