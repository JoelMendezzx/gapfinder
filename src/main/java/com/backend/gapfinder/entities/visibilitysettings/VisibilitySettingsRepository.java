package com.backend.gapfinder.entities.visibilitysettings;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VisibilitySettingsRepository extends JpaRepository<VisibilitySettingsEntity, Long> {

    Optional<VisibilitySettingsEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}