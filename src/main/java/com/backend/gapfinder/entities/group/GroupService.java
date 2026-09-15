package com.backend.gapfinder.entities.group;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;

    public GroupService(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<GroupEntity> getAll() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GroupEntity> getByUser(Long userId) {
        userService.getById(userId);
        return groupRepository.findByMembers_Id(userId);
    }

    @Transactional(readOnly = true)
    public GroupEntity getById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El grupo con id " + id + " no existe"));
    }

    @Transactional
    public GroupEntity create(Long creatorId, GroupEntity group) {
        if (group == null) {
            throw new IllegalArgumentException("El grupo es obligatorio");
        }

        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo es obligatorio");
        }

        UserEntity creator = userService.getById(creatorId);

        group.setId(null);
        group.setCreator(creator);
        group.setCreatedAt(LocalDateTime.now());

        if (group.getMembers() == null) {
            group.setMembers(new java.util.ArrayList<>());
        }

        if (!group.getMembers().contains(creator)) {
            group.getMembers().add(creator);
        }

        return groupRepository.save(group);
    }

    @Transactional
    public GroupEntity update(Long id, GroupEntity group) {
        GroupEntity existing = getById(id);

        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo es obligatorio");
        }

        existing.setName(group.getName());
        return groupRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        GroupEntity existing = getById(id);
        groupRepository.delete(existing);
    }
}
