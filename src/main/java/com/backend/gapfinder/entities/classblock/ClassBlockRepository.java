package com.backend.gapfinder.entities.classblock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassBlockRepository extends JpaRepository<ClassBlockEntity, Long> {

    List<ClassBlockEntity> findByUserId(Long userId);
}