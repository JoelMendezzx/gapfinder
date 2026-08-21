package com.backend.gapfinder.entities.opentable;

import com.backend.gapfinder.entities.building.BuildingEntity;
import com.backend.gapfinder.entities.building.BuildingService;
import com.backend.gapfinder.entities.activity.ActivityEntity;
import com.backend.gapfinder.entities.activity.ActivityService;
import com.backend.gapfinder.entities.gap.GapService;
import com.backend.gapfinder.entities.group.GroupEntity;
import com.backend.gapfinder.entities.group.GroupService;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.notification.NotificationService;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantEntity;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpenTableService {

    private final OpenTableRepository openTableRepository;
    private final OpenTableParticipantService participantService;
    private final UserService userService;
    private final BuildingService buildingService;
    private final GroupService groupService;
    private final GapService gapService;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    public OpenTableService(OpenTableRepository openTableRepository,
                            OpenTableParticipantService participantService,
                            UserService userService,
                            BuildingService buildingService,
                            GroupService groupService,
                            GapService gapService,
                            ActivityService activityService,
                        NotificationService notificationService) {
        this.openTableRepository = openTableRepository;
        this.participantService = participantService;
        this.userService = userService;
        this.buildingService = buildingService;
        this.groupService = groupService;
        this.gapService = gapService;
        this.activityService = activityService;
        this.notificationService = notificationService;
    }

    // Crear una Open Table (pública o privada de grupo), validando GAP activo del creador
    // y uniéndolo automáticamente como primer participante; si es privada, solo se crea
    // si hay más de 1 miembro del grupo libre en este momento, e invita solo a los disponibles
        @Transactional
    public OpenTableEntity create(Long creatorId, Long buildingId, Long groupId,
                                 OpenTableEntity openTable, Integer durationMinutes) {
        log.info("Inicia creación de Open Table para usuario con id = {}", creatorId);

        validateOpenTableData(openTable, durationMinutes);

        UserEntity creator = userService.getById(creatorId);
        BuildingEntity building = buildingService.getById(buildingId);

        if (openTable.getActivity() == null || openTable.getActivity().getId() == null) {
            throw new IllegalArgumentException("La actividad es obligatoria");
        }

        ActivityEntity activity = activityService.getById(openTable.getActivity().getId());
        openTable.setActivity(activity);

        GroupEntity group = null;
        if (openTable.isPrivateTable()) {
            if (groupId == null) {
                throw new IllegalArgumentException("Una Open Table privada debe tener un grupo");
            }
            group = groupService.getById(groupId);

            boolean belongsToGroup = group.getMembers().stream()
                    .anyMatch(member -> member.getId().equals(creatorId));

            if (!belongsToGroup) {
                throw new IllegalArgumentException("El creador no pertenece a este grupo");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        if (gapService.getActiveGap(creatorId, now, endTime).isEmpty()) {
            throw new IllegalStateException("El usuario no tiene un GAP que cubra toda la duración de la Open Table");
        }

        openTable.setId(null);
        openTable.setCreator(creator);
        openTable.setBuilding(building);
        openTable.setGroup(openTable.isPrivateTable() ? group : null);
        openTable.setStartTime(now);
        openTable.setEndTime(endTime);
        openTable.setCreatedAt(now);
        openTable.setStatus(OpenTableStatusEnum.ACTIVE);

        OpenTableEntity saved = openTableRepository.save(openTable);
        participantService.join(saved, creator);

        if (saved.isPrivateTable()) {
            for (UserEntity member : group.getMembers()) {
                if (member.getId().equals(creatorId)) {
                    continue;
                }

                boolean disponible = gapService.getActiveGap(
                        member.getId(), saved.getStartTime(), saved.getEndTime()
                ).isPresent();

                if (!disponible) {
                    continue;
                }

                participantService.invite(saved, member);

                notificationService.create(
                        member.getId(),
                        NotificationTypeEnum.OPEN_TABLE_INVITE,
                        saved.getId(),
                        creator.getName() + " propuso una Open Table en tu grupo"
                );
            }
        }

        log.info("Termina creación de Open Table con id = {}", saved.getId());
        return saved;
    }

    // Consultar una Open Table por id
    @Transactional(readOnly = true)
    public OpenTableEntity getById(Long id) {
        log.info("Consultando Open Table con id = {}", id);

        return openTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La Open Table con id " + id + " no existe"));
    }

    // Listar las Open Tables públicas activas de un edificio,
    // mostrando primero las que tienen más tiempo restante
    @Transactional(readOnly = true)
    public List<OpenTableEntity> getAllByBuilding(Long buildingId) {
        log.info("Consultando Open Tables del edificio con id = {}", buildingId);

        buildingService.getById(buildingId);

        return openTableRepository.findByBuildingIdAndPrivateTableFalseAndStatusAndEndTimeAfterOrderByEndTimeDesc(
                buildingId,
                OpenTableStatusEnum.ACTIVE,
                LocalDateTime.now()
        );
    }

    // Contar cuántas Open Tables públicas activas hay en cada edificio del campus
    @Transactional(readOnly = true)
    public Map<String, Long> getActiveOnes() {
        log.info("Consultando Open Tables públicas activas del campus");

        List<OpenTableEntity> activeTables = openTableRepository.findByStatusAndPrivateTableFalseAndEndTimeAfter(
                OpenTableStatusEnum.ACTIVE,
                LocalDateTime.now()
        );

        return activeTables.stream()
                .collect(Collectors.groupingBy(
                        table -> table.getBuilding().getName(),
                        Collectors.counting()
                ));
    }

    // Unirse a una Open Table pública, validando que esté activa, no haya terminado,
    // el usuario tenga GAP activo, y no esté ya adentro
    // Unirse a una Open Table pública, validando que esté activa, no haya terminado,
    // el usuario tenga GAP activo, y no esté ya adentro
    @Transactional
    public OpenTableParticipantEntity join(Long openTableId, Long userId) {
        log.info("Usuario {} intenta unirse a Open Table {}", userId, openTableId);

        OpenTableEntity openTable = getById(openTableId);
        UserEntity user = userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (openTable.isPrivateTable()) {
            throw new IllegalArgumentException("Esta Open Table es privada");
        }

        if (openTable.getStatus() != OpenTableStatusEnum.ACTIVE) {
            throw new IllegalStateException("Esta Open Table no está activa");
        }

        if (!openTable.getEndTime().isAfter(now)) {
            throw new IllegalStateException("Esta Open Table ya terminó");
        }

        if (gapService.getActiveGap(userId, now, now).isEmpty()) {
            throw new IllegalStateException("El usuario no tiene un GAP activo");
        }

        if (participantService.isUserIn(openTableId, userId)) {
            throw new IllegalStateException("El usuario ya está dentro de esta Open Table");
        }

        OpenTableParticipantEntity nuevoParticipante = participantService.join(openTable, user);

        List<OpenTableParticipantEntity> participantesActuales = participantService.getByOpenTable(openTableId);

        for (OpenTableParticipantEntity p : participantesActuales) {
            boolean esElMismoQueSeUnio = p.getUser().getId().equals(userId);
            boolean estaDentro = p.getRsvp() == ResponseStatusEnum.IN;

            if (!esElMismoQueSeUnio && estaDentro) {
                notificationService.create(
                        p.getUser().getId(),
                        NotificationTypeEnum.OPEN_TABLE_JOIN,
                        openTable.getId(),
                        user.getName() + " se unió a la Open Table"
                );
            }
        }

        return nuevoParticipante;
    }

    // Responder (aceptar/declinar) una invitación a una Open Table privada de grupo
    @Transactional
    public OpenTableParticipantEntity respondToInvite(Long openTableId, Long userId, boolean accept) {
        OpenTableEntity openTable = getById(openTableId);

        if (!openTable.isPrivateTable()) {
            throw new IllegalArgumentException("Esta Open Table es pública");
        }

        LocalDateTime now = LocalDateTime.now();

        if (openTable.getStatus() != OpenTableStatusEnum.ACTIVE || !openTable.getEndTime().isAfter(now)) {
            throw new IllegalStateException("Esta Open Table ya no está activa");
        }

        if (accept && gapService.getActiveGap(userId, now, now).isEmpty()) {
            throw new IllegalStateException("El usuario no tiene un GAP activo");
        }

        return participantService.respondToInvite(openTableId, userId, accept);
    }

    // Salir de una Open Table; si el que sale es el creador y ya no queda nadie más, se cierra la mesa
    @Transactional
    public OpenTableParticipantEntity leave(Long openTableId, Long userId) {
        log.info("Usuario {} sale de Open Table {}", userId, openTableId);

        OpenTableEntity openTable = getById(openTableId);
        OpenTableParticipantEntity participant = participantService.leave(openTableId, userId);

        boolean creatorLeaving = openTable.getCreator().getId().equals(userId);

        if (creatorLeaving) {
            long remainingParticipants = participantService.countActiveParticipants(openTableId);

            if (remainingParticipants == 0) {
                openTable.setStatus(OpenTableStatusEnum.ENDED);
                openTableRepository.save(openTable);
            }
        }

        return participant;
    }

    // Job automático (cada 5 minutos): cierra todas las Open Tables activas cuyo tiempo ya pasó
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void expireEndedOpenTables() {
        log.info("Inicia proceso de expirar Open Tables");

        LocalDateTime now = LocalDateTime.now();
        List<OpenTableEntity> expired = openTableRepository.findByStatusAndEndTimeBefore(
                OpenTableStatusEnum.ACTIVE,
                now
        );

        expired.forEach(table -> table.setStatus(OpenTableStatusEnum.ENDED));
        openTableRepository.saveAll(expired);

        log.info("Open Tables finalizadas = {}", expired.size());
    }

    // Sugerir actividades según intereses y duración disponible del GAP
    @Transactional(readOnly = true)
    public List<ActivityEntity> calculateSuggestedActivities(Long userId, Integer gapDurationMinutes) {
        if (gapDurationMinutes == null || gapDurationMinutes <= 0) {
            throw new IllegalArgumentException("La duración del GAP debe ser mayor a 0");
        }

        UserEntity user = userService.getById(userId);

        Set<Long> interestIds = user.getInterests().stream()
                .map(InterestEntity::getId)
                .collect(Collectors.toSet());

        return activityService.getAll().stream()
                .filter(activity -> activity.getDurationMinutes() <= gapDurationMinutes)
                .filter(activity -> activity.getInterest() == null
                        || interestIds.contains(activity.getInterest().getId()))
            .sorted(Comparator.comparing(activity -> activity.getInterest() == null))
                .toList();
    }

    // Validar que la Open Table tenga actividad y duración válidas antes de crearla
    private void validateOpenTableData(OpenTableEntity openTable, Integer durationMinutes) {
        if (openTable == null) {
            throw new IllegalArgumentException("La Open Table es obligatoria");
        }

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
    }

}