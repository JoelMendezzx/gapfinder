package com.backend.gapfinder.entities.friendship;

import com.backend.gapfinder.entities.gap.GapRepository;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.FriendshipStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final GapRepository gapRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService, GapRepository gapRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
        this.gapRepository = gapRepository;
    }

    // Enviar una solicitud de amistad
    @Transactional
    public FriendshipEntity sendRequest(Long requesterId, Long addresseeId) {
        log.info("Inicia proceso de enviar solicitud de amistad de {} hacia {}", requesterId, addresseeId);

        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Un usuario no puede enviarse solicitud a sí mismo");
        }

        UserEntity requester = userService.getById(requesterId);
        UserEntity addressee = userService.getById(addresseeId);

        Optional<FriendshipEntity> existente = buscarRelacionEntre(requesterId, addresseeId);
        if (existente.isPresent() && existente.get().getStatus() != FriendshipStatusEnum.REJECTED) {
            throw new IllegalArgumentException("Ya existe una relación de amistad entre estos usuarios");
        }

        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatusEnum.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());

        log.info("Termina proceso de enviar solicitud de amistad de {} hacia {}", requesterId, addresseeId);
        return friendshipRepository.save(friendship);
    }

    // Aceptar una solicitud de amistad
    @Transactional
    public FriendshipEntity acceptRequest(Long friendshipId) {
        log.info("Inicia proceso de aceptar solicitud de amistad con id = {}", friendshipId);

        FriendshipEntity friendship = getById(friendshipId);
        validatePending(friendship);

        friendship.setStatus(FriendshipStatusEnum.ACCEPTED);

        log.info("Termina proceso de aceptar solicitud de amistad con id = {}", friendshipId);
        return friendshipRepository.save(friendship);
    }

    // Rechazar una solicitud de amistad
    @Transactional
    public FriendshipEntity rejectRequest(Long friendshipId) {
        log.info("Inicia proceso de rechazar solicitud de amistad con id = {}", friendshipId);

        FriendshipEntity friendship = getById(friendshipId);
        validatePending(friendship);

        friendship.setStatus(FriendshipStatusEnum.REJECTED);

        log.info("Termina proceso de rechazar solicitud de amistad con id = {}", friendshipId);
        return friendshipRepository.save(friendship);
    }

    // Eliminar un amigo (borrar la relación por completo)
    @Transactional
    public void delete(Long friendshipId) {
        log.info("Inicia proceso de eliminación de la amistad con id = {}", friendshipId);

        FriendshipEntity friendship = getById(friendshipId);
        friendshipRepository.delete(friendship);

        log.info("Termina proceso de eliminación de la amistad con id = {}", friendshipId);
    }

    // Listar los amigos (amistades aceptadas) de un usuario
    @Transactional
    public List<UserEntity> getFriendsByUser(Long userId) {
        log.info("Inicia proceso de consultar los amigos del usuario con id = {}", userId);

        userService.getById(userId);

        return friendshipRepository.findByRequesterIdOrAddresseeId(userId, userId).stream()
                .filter(f -> f.getStatus() == FriendshipStatusEnum.ACCEPTED)
                .map(f -> otroLado(f, userId))
                .toList();
    }

    // Listar los amigos que tienen un GAP activo en este momento
    @Transactional(readOnly = true)
    public List<UserEntity> getFriendsWithActiveGap(Long userId) {

        log.info("Consultando amigos libres ahora mismo del usuario con id = {}", userId);

        List<UserEntity> amigos = getFriendsByUser(userId);

        List<Long> friendIds = amigos.stream()
                .map(UserEntity::getId)
                .toList();

        return gapRepository.findAvailableFriends(
                friendIds,
                LocalDateTime.now()
        );
    }

    // Consultar una amistad por id
    private FriendshipEntity getById(Long id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La amistad con id " + id + " no existe"));
    }

    // Buscar si ya existe una relación entre dos usuarios (en cualquier dirección)
    private Optional<FriendshipEntity> buscarRelacionEntre(Long userAId, Long userBId) {
        Optional<FriendshipEntity> directa = friendshipRepository.findByRequesterIdAndAddresseeId(userAId, userBId);
        if (directa.isPresent()) {
            return directa;
        }
        return friendshipRepository.findByRequesterIdAndAddresseeId(userBId, userAId);
    }

    // Obtener el usuario "del otro lado" de una amistad
    private UserEntity otroLado(FriendshipEntity friendship, Long userId) {
        return friendship.getRequester().getId().equals(userId)
                ? friendship.getAddressee()
                : friendship.getRequester();
    }

    // Validar que la solicitud esté pendiente antes de aceptar/rechazar
    private void validatePending(FriendshipEntity friendship) {
        if (friendship.getStatus() != FriendshipStatusEnum.PENDING) {
            throw new IllegalArgumentException("La solicitud ya fue respondida");
        }
    }
}