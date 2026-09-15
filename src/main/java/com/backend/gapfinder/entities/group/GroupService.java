package com.backend.gapfinder.entities.group;

import com.backend.gapfinder.entities.friendship.FriendshipService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.exceptions.NotFoundException;
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

        // Validar que todos los miembros iniciales (menos el creador) sean amigos del creador
        List<Long> friendIds = friendshipService.getFriendsByUser(creatorId).stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        if (group.getMembers() != null) {
            for (UserEntity member : group.getMembers()) {
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
        List<UserEntity> resolvedMembers = new java.util.ArrayList<>();
        if (group.getMembers() != null) {
            for (UserEntity member : group.getMembers()) {
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
    public GroupEntity addMember(Long groupId, Long requesterId, Long newMemberId) {
        GroupEntity group = getById(groupId);

        if (!group.getCreator().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Solo el creador del grupo puede invitar miembros");
        }

        UserEntity newMember = userService.getById(newMemberId);

        boolean alreadyMember = group.getMembers().stream()
                .anyMatch(member -> member.getId().equals(newMemberId));
        if (alreadyMember) {
            throw new IllegalStateException("El usuario ya es miembro de este grupo");
        }

        List<Long> friendIds = friendshipService.getFriendsByUser(group.getCreator().getId()).stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        if (!friendIds.contains(newMemberId)) {
            throw new IllegalArgumentException("El usuario no es amigo del creador del grupo");
        }

        group.getMembers().add(newMember);
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