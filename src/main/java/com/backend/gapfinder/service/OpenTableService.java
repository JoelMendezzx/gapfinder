package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationPublisher;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.model.BuildingModel;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.model.OpenTableParticipantModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.OpenTableRepository;
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
    private final ActivityService activityService;
    private final NotificationPublisher notificationPublisher;

    public OpenTableService(OpenTableRepository openTableRepository,
                            OpenTableParticipantService participantService,
                            UserService userService,
                            BuildingService buildingService,
                            ActivityService activityService,
                            NotificationPublisher notificationPublisher) {
        this.openTableRepository = openTableRepository;
        this.participantService = participantService;
        this.userService = userService;
        this.buildingService = buildingService;
        this.activityService = activityService;
        this.notificationPublisher = notificationPublisher;
    }

    // Crear una Open Table pública y unir automáticamente al creador.
    @Transactional
    public OpenTableModel create(Long creatorId, Long buildingId,
                                 OpenTableModel openTable, Integer durationMinutes) {
        log.info("Inicia creación de Open Table para usuario con id = {}", creatorId);

        validateOpenTableData(openTable, durationMinutes);

        UserModel creator = userService.getById(creatorId);
        BuildingModel building = buildingService.getById(buildingId);

        if (openTable.getActivity() == null || openTable.getActivity().getId() == null) {
            throw new IllegalArgumentException("La actividad es obligatoria");
        }

        ActivityModel activity = activityService.getById(openTable.getActivity().getId());
        openTable.setActivity(activity);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        openTable.setId(null);
        openTable.setCreator(creator);
        openTable.setBuilding(building);
        openTable.setStartTime(now);
        openTable.setEndTime(endTime);
        openTable.setCreatedAt(now);
        openTable.setStatus(OpenTableStatusEnum.ACTIVE);

        OpenTableModel saved = openTableRepository.save(openTable);
        participantService.join(saved, creator);

        log.info("Termina creación de Open Table con id = {}", saved.getId());
        return saved;
    }

    // Consultar una Open Table por id
    @Transactional(readOnly = true)
    public OpenTableModel getById(Long id) {
        log.info("Consultando Open Table con id = {}", id);

        OpenTableModel openTable = openTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La Open Table con id " + id + " no existe"));

        // Inicializa participantes y sus usuarios antes de que el controller
        // transforme la entidad en DTO fuera de esta transacción.
        openTable.getParticipants().size();
        openTable.getParticipants().forEach(participant -> participant.getUser().getId());
        return openTable;
    }

    // Todas las Open Tables en las que participa un usuario, activas o terminadas.
    @Transactional(readOnly = true)
    public List<OpenTableModel> getAllByUser(Long userId) {
        userService.getById(userId);
        return participantService.getByUser(userId).stream()
                .map(OpenTableParticipantModel::getOpenTable)
                .toList();
    }

    // Todas las Open Tables creadas por un usuario, activas o terminadas.
    @Transactional(readOnly = true)
    public List<OpenTableModel> getCreatedByUser(Long userId) {
        userService.getById(userId);
        return openTableRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
    }

    // Open Tables activas disponibles para descubrir, excluyendo las propias y las ya ocupadas.
    @Transactional(readOnly = true)
    public List<OpenTableModel> getDiscoverableForUser(Long userId) {
        userService.getById(userId);
        return openTableRepository.findDiscoverableForUser(
                userId,
                OpenTableStatusEnum.ACTIVE,
                ResponseStatusEnum.IN,
                LocalDateTime.now());
    }

    // Listar las Open Tables públicas activas de un edificio,
    // mostrando primero las que tienen más tiempo restante
    @Transactional(readOnly = true)
    public List<OpenTableModel> getAllByBuilding(Long buildingId) {
        log.info("Consultando Open Tables del edificio con id = {}", buildingId);

        buildingService.getById(buildingId);

        return openTableRepository.findByBuildingIdAndStatusAndEndTimeAfterOrderByEndTimeDesc(
                buildingId,
                OpenTableStatusEnum.ACTIVE,
                LocalDateTime.now()
        );
    }

    // Contar cuántas Open Tables públicas activas hay en cada edificio del campus
    @Transactional(readOnly = true)
    public Map<String, Long> getActiveOnes() {
        log.info("Consultando Open Tables públicas activas del campus");

        List<OpenTableModel> activeTables = openTableRepository.findByStatusAndEndTimeAfter(
                OpenTableStatusEnum.ACTIVE,
                LocalDateTime.now()
        );

        return activeTables.stream()
                .collect(Collectors.groupingBy(
                        table -> table.getBuilding().getName(),
                        Collectors.counting()
                ));
    }

    // Unirse a una Open Table pública, validando que esté activa, que no haya terminado
    // y que el usuario no esté ya adentro.
    @Transactional
    public OpenTableParticipantModel join(Long openTableId, Long userId) {
        log.info("Usuario {} intenta unirse a Open Table {}", userId, openTableId);

        OpenTableModel openTable = getById(openTableId);
        UserModel user = userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (openTable.getStatus() != OpenTableStatusEnum.ACTIVE) {
            throw new IllegalStateException("Esta Open Table no está activa");
        }

        if (!openTable.getEndTime().isAfter(now)) {
            throw new IllegalStateException("Esta Open Table ya terminó");
        }

        if (participantService.isUserIn(openTableId, userId)) {
            throw new IllegalStateException("El usuario ya está dentro de esta Open Table");
        }

        OpenTableParticipantModel nuevoParticipante = participantService.join(openTable, user);

        List<OpenTableParticipantModel> participantesActuales = participantService.getByOpenTable(openTableId);

        for (OpenTableParticipantModel p : participantesActuales) {
            boolean esElMismoQueSeUnio = p.getUser().getId().equals(userId);
            boolean estaDentro = p.getRsvp() == ResponseStatusEnum.IN;

            if (!esElMismoQueSeUnio && estaDentro) {
                notificationPublisher.publish(new NotificationEvent(
                        p.getUser().getId(),
                        NotificationTypeEnum.OPEN_TABLE_JOIN,
                        openTable.getId(),
                        user.getName() + " se unió a la Open Table"
                ));
            }
        }

        return nuevoParticipante;
    }

    // Salir de una Open Table; si el que sale es el creador y ya no queda nadie más, se cierra la mesa
    @Transactional
    public OpenTableParticipantModel leave(Long openTableId, Long userId) {
        log.info("Usuario {} sale de Open Table {}", userId, openTableId);

        OpenTableModel openTable = getById(openTableId);
        OpenTableParticipantModel participant = participantService.leave(openTableId, userId);

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
        List<OpenTableModel> expired = openTableRepository.findByStatusAndEndTimeBefore(
                OpenTableStatusEnum.ACTIVE,
                now
        );

        expired.forEach(table -> table.setStatus(OpenTableStatusEnum.ENDED));
        openTableRepository.saveAll(expired);

        log.info("Open Tables finalizadas = {}", expired.size());
    }

    // Sugerir actividades según intereses y duración elegida para la Open Table
    @Transactional(readOnly = true)
    public List<ActivityModel> calculateSuggestedActivities(Long userId, Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }

        UserModel user = userService.getById(userId);

        Set<Long> interestIds = user.getInterests().stream()
                .map(InterestModel::getId)
                .collect(Collectors.toSet());

        return activityService.getAll().stream()
                .filter(activity -> activity.getDurationMinutes() <= durationMinutes)
                .filter(activity -> activity.getInterest() == null
                        || interestIds.contains(activity.getInterest().getId()))
            .sorted(Comparator.comparing(activity -> activity.getInterest() == null))
                .toList();
    }

    // Validar que la Open Table tenga actividad y duración válidas antes de crearla
    private void validateOpenTableData(OpenTableModel openTable, Integer durationMinutes) {
        if (openTable == null) {
            throw new IllegalArgumentException("La Open Table es obligatoria");
        }

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
    }


    // Contar cuántas Open Tables se han creado desde una fecha (para las estadísticas de abandono)
    @Transactional(readOnly = true)
    public long countCreatedSince(LocalDateTime since) {
        return openTableRepository.countByCreatedAtGreaterThanEqual(since);
    }

}