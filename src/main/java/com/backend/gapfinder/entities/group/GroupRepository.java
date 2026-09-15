package com.backend.gapfinder.entities.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    // Grupos a los que pertenece un usuario (como miembro, incluyendo al creador si se agregó como tal)
    List<GroupEntity> findByMembers_Id(Long userId);
}