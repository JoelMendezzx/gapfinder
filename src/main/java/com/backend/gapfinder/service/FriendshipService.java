package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.FriendshipStatusEnum;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationPublisher;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.FriendshipModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.FriendshipRepository;
import com.backend.gapfinder.repository.GapRepository;
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
    private final NotificationPublisher notificationPublisher;

    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService,
                             GapRepository gapRepository, NotificationPublisher notificationPublisher) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
        this.gapRepository = gapRepository;
        this.notificationPublisher = notificationPublisher;
    }

    // Enviar una solicitud de amistad
    @Transactional
    public FriendshipModel sendRequest(Long requesterId, Long addresseeId) {
        log.info("Inicia proceso de enviar solicitud de amistad de {} hacia {}", requesterId, addresseeId);

        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Un usuario no puede enviarse solicitud a sí mismo");
        }

        UserModel requester = userService.getById(requesterId);
        UserModel addressee = userService.getById(addresseeId);

        Optional<FriendshipModel> existente = buscarRelacionEntre(requesterId, addresseeId);
        if (existente.isPresent() && existente.get().getStatus() != FriendshipStatusEnum.REJECTED) {
            throw new IllegalArgumentException("Ya existe una relación de amistad entre estos usuarios");
        }

        FriendshipModel friendship = new FriendshipModel();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatusEnum.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());

        FriendshipModel saved = friendshipRepository.save(friendship);

        notificationPublisher.publish(new NotificationEvent(
                addresseeId,
                NotificationTypeEnum.FRIEND_REQUEST,
                saved.getId(),
                requester.getName() + " te envió una solicitud de amistad"
        ));

        log.info("Termina proceso de enviar solicitud de amistad de {} hacia {}", requesterId, addresseeId);
        return saved;
    }

    // Aceptar una solicitud de amistad con validación de destinatario
    @Transactional
    public FriendshipModel acceptRequest(Long friendshipId, Long userId) {
        log.info("Inicia proceso de aceptar solicitud id = {} por el usuario = {}", friendshipId, userId);

        FriendshipModel friendship = getById(friendshipId);

        // Validación: Solo el destinatario (addressee) puede aceptar la solicitud
        validateAddressee(friendship, userId);
        validatePending(friendship);

        friendship.setStatus(FriendshipStatusEnum.ACCEPTED);

        FriendshipModel saved = friendshipRepository.save(friendship);

        notificationPublisher.publish(new NotificationEvent(
                saved.getRequester().getId(),
                NotificationTypeEnum.FRIEND_ACCEPTED,
                saved.getId(),
                saved.getAddressee().getName() + " aceptó tu solicitud de amistad"
        ));

        log.info("Termina proceso de aceptar solicitud de amistad con id = {}", friendshipId);
        return saved;
    }

    // Rechazar una solicitud de amistad con validación de destinatario
    @Transactional
    public FriendshipModel rejectRequest(Long friendshipId, Long userId) {
        log.info("Inicia proceso de rechazar solicitud id = {} por el usuario = {}", friendshipId, userId);

        FriendshipModel friendship = getById(friendshipId);

        // Validación: Solo el destinatario (addressee) puede rechazar la solicitud
        validateAddressee(friendship, userId);
        validatePending(friendship);

        friendship.setStatus(FriendshipStatusEnum.REJECTED);

        log.info("Termina proceso de rechazar solicitud de amistad con id = {}", friendshipId);
        return friendshipRepository.save(friendship);
    }

    // Eliminar un amigo (borrar la relación por completo)
    @Transactional
    public void delete(Long friendshipId) {
        log.info("Inicia proceso de eliminación de la amistad con id = {}", friendshipId);

        FriendshipModel friendship = getById(friendshipId);
        friendshipRepository.delete(friendship);

        log.info("Termina proceso de eliminación de la amistad con id = {}", friendshipId);
    }

    // Listar los amigos (amistades aceptadas) de un usuario
    @Transactional
    public List<UserModel> getFriendsByUser(Long userId) {
        log.info("Inicia proceso de consultar los amigos del usuario con id = {}", userId);

        userService.getById(userId);

        return friendshipRepository.findByRequesterIdOrAddresseeId(userId, userId).stream()
                .filter(f -> f.getStatus() == FriendshipStatusEnum.ACCEPTED)
                .map(f -> otroLado(f, userId))
                .toList();
    }

    // Listar los amigos que tienen un GAP activo en este momento
    @Transactional(readOnly = true)
    public List<UserModel> getFriendsWithActiveGap(Long userId) {

        log.info("Consultando amigos libres ahora mismo del usuario con id = {}", userId);

        List<UserModel> amigos = getFriendsByUser(userId);

        List<Long> friendIds = amigos.stream()
                .map(UserModel::getId)
                .toList();

        return gapRepository.findAvailableFriends(
                friendIds,
                LocalDateTime.now()
        );
    }

    // Consultar una amistad por id
    private FriendshipModel getById(Long id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La amistad con id " + id + " no existe"));
    }

    // Buscar si ya existe una relación entre dos usuarios (en cualquier dirección)
    private Optional<FriendshipModel> buscarRelacionEntre(Long userAId, Long userBId) {
        Optional<FriendshipModel> directa = friendshipRepository.findByRequesterIdAndAddresseeId(userAId, userBId);
        if (directa.isPresent()) {
            return directa;
        }
        return friendshipRepository.findByRequesterIdAndAddresseeId(userBId, userAId);
    }

    // Obtener el usuario "del otro lado" de una amistad
    private UserModel otroLado(FriendshipModel friendship, Long userId) {
        return friendship.getRequester().getId().equals(userId)
                ? friendship.getAddressee()
                : friendship.getRequester();
    }

    // Validar que la solicitud esté pendiente antes de aceptar/rechazar
    private void validatePending(FriendshipModel friendship) {
        if (friendship.getStatus() != FriendshipStatusEnum.PENDING) {
            throw new IllegalArgumentException("La solicitud ya fue respondida");
        }
    }

    // Validar que el usuario que responde sea el destinatario (addressee)
    private void validateAddressee(FriendshipModel friendship, Long userId) {
        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new IllegalArgumentException("Solo el destinatario de la solicitud puede responderla");
        }
    }
}