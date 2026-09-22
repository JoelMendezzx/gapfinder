package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupModel, Long> {

    // Grupos a los que pertenece un usuario (como miembro, incluyendo al creador si se agregó como tal)
    List<GroupModel> findByMembers_Id(Long userId);
}