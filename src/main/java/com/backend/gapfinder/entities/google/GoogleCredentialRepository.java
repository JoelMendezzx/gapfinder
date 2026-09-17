package com.backend.gapfinder.entities.google;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleCredentialRepository extends JpaRepository<GoogleCredentialEntity, Long> {
    Optional<GoogleCredentialEntity> findByUserId(Long userId);
}
