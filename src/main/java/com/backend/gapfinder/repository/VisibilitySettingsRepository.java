package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.VisibilitySettingsModel;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VisibilitySettingsRepository extends JpaRepository<VisibilitySettingsModel, Long> {

    Optional<VisibilitySettingsModel> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}