package com.backend.gapfinder.entities.interest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<InterestEntity, Long> {

    Optional<InterestEntity> findByName(String name);
}
