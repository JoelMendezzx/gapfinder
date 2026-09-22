package com.backend.gapfinder.service;

import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.GroupModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;
    private final FriendshipService friendshipService;

    public GroupService(GroupRepository groupRepository, UserService userService, FriendshipService friendshipService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @Transactional(readOnly = true)
    public List<GroupModel> getAll() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GroupModel> getByUser(Long userId) {
        userService.getById(userId);
        return groupRepository.findByMembers_Id(userId);
    }

    @Transactional(readOnly = true)
    public GroupModel getById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El grupo con id " + id + " no existe"));
    }

    @Transactional
    public GroupModel create(Long creatorId, GroupModel group) {
        if (group == null) {
            throw new IllegalArgumentException("El grupo es obligatorio");
        }

        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo es obligatorio");
        }

        UserModel creator = userService.getById(creatorId);

        // Validar que todos los miembros iniciales (menos el creador) sean amigos del creador
        List<Long> friendIds = friendshipService.getFriendsByUser(creatorId).stream()
                .map(UserModel::getId)
                .collect(Collectors.toList());

        if (group.getMembers() != null) {
            for (UserModel member : group.getMembers()) {
                if (member.getId() == null) {
                    throw new IllegalArgumentException("Cada miembro debe tener un id válido");
                }
                if (!member.getId().equals(creatorId) && !friendIds.contains(member.getId())) {
                    throw new IllegalArgumentException(
                            "El usuario con id " + member.getId() + " no es amigo del creador");
                }
            }
        }

        // Resolver los miembros a entidades reales de la BD (evita pasar objetos "sueltos")
        List<UserModel> resolvedMembers = new java.util.ArrayList<>();
        if (group.getMembers() != null) {
            for (UserModel member : group.getMembers()) {
                resolvedMembers.add(userService.getById(member.getId()));
            }
        }

        group.setId(null);
        group.setCreator(creator);
        group.setCreatedAt(LocalDateTime.now());
        group.setMembers(resolvedMembers);

        if (!group.getMembers().contains(creator)) {
            group.getMembers().add(creator);
        }

        return groupRepository.save(group);
    }

    // Agrega un miembro nuevo a un grupo ya existente, validando que sea amigo del creador
    @Transactional
    public GroupModel addMember(Long groupId, Long requesterId, Long newMemberId) {
        GroupModel group = getById(groupId);

        if (!group.getCreator().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Solo el creador del grupo puede invitar miembros");
        }

        UserModel newMember = userService.getById(newMemberId);

        boolean alreadyMember = group.getMembers().stream()
                .anyMatch(member -> member.getId().equals(newMemberId));
        if (alreadyMember) {
            throw new IllegalStateException("El usuario ya es miembro de este grupo");
        }

        List<Long> friendIds = friendshipService.getFriendsByUser(group.getCreator().getId()).stream()
                .map(UserModel::getId)
                .collect(Collectors.toList());

        if (!friendIds.contains(newMemberId)) {
            throw new IllegalArgumentException("El usuario no es amigo del creador del grupo");
        }

        group.getMembers().add(newMember);
        return groupRepository.save(group);
    }

    @Transactional
    public GroupModel update(Long id, GroupModel group) {
        GroupModel existing = getById(id);

        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo es obligatorio");
        }

        existing.setName(group.getName());
        return groupRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        GroupModel existing = getById(id);
        groupRepository.delete(existing);
    }
}