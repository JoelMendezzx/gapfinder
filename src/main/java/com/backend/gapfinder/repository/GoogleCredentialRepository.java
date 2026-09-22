package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.GoogleCredentialModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleCredentialRepository extends JpaRepository<GoogleCredentialModel, Long> {
    Optional<GoogleCredentialModel> findByUserId(Long userId);
}
