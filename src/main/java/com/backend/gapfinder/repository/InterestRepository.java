package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.InterestModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<InterestModel, Long> {

    Optional<InterestModel> findByName(String name);
}
