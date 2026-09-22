package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.DayOfWeekEnum;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationPublisher;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.ClassBlockModel;
import com.backend.gapfinder.model.GapModel;
import com.backend.gapfinder.model.MatchModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.GapRepository;
import com.backend.gapfinder.repository.MatchRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class GapService {

    // Ventana de operación del campus
    private static final LocalTime CAMPUS_OPEN = LocalTime.of(6, 30);
    private static final LocalTime CAMPUS_CLOSE = LocalTime.of(5, 55);
    private static final long SOON_WINDOW_MINUTES = 20;

    private final GapRepository gapRepository;
    private final MatchRepository matchRepository;
    private final UserService userService;
    private final ClassBlockService classBlockService;
    private final NotificationPublisher notificationPublisher;

    public GapService(GapRepository gapRepository, MatchRepository matchRepository, UserService userService, ClassBlockService classBlockService, NotificationPublisher notificationPublisher) {
        this.gapRepository = gapRepository;
        this.matchRepository = matchRepository;
        this.userService = userService;
        this.classBlockService = classBlockService;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Consulta un GAP por su id. Lanza NotFoundException si no existe.
     */
    @Transactional
    public GapModel getById(Long id) {
        log.info("Inicia proceso de consultar el GAP con id = {}", id);
        return gapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El GAP con id " + id + " no existe"));
    }

    /**
     * Devuelve el historial completo de GAPs (pasados y futuros) de un usuario,
     * sin filtrar por fecha.
     */
    @Transactional
    public List<GapModel> getAllByUser(Long userId) {
        log.info("Inicia proceso de consultar el historial de GAPs del usuario con id = {}", userId);

        userService.getById(userId);

        return gapRepository.findByUserId(userId);
    }

    /**
     * Devuelve únicamente los GAPs del usuario que caen dentro del día de hoy
     * (desde las 00:00 hasta las 23:59:59.999).
     */
    @Transactional
    public List<GapModel> getTodayByUser(Long userId) {
        log.info("Inicia proceso de consultar los GAPs de hoy del usuario con id = {}", userId);

        userService.getById(userId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return gapRepository.findByUserIdAndStartTimeBetween(userId, start, end);
    }

    /**
     * Crea un GAP manualmente para un usuario (a diferencia de calculateGapsFromSchedule,
     * que los genera automáticamente a partir del horario de clases). Valida que las
     * horas sean coherentes y calcula la duración en minutos antes de guardar.
     */
    @Transactional
    public GapModel create(Long userId, GapModel gap) {
        log.info("Inicia proceso de creación de un GAP para el usuario con id = {}", userId);

        validateGapData(gap);

        UserModel user = userService.getById(userId);
        gap.setId(null);
        gap.setUser(user);
        gap.setDurationMinutes((int) java.time.Duration.between(gap.getStartTime(), gap.getEndTime()).toMinutes());

        log.info("Termina proceso de creación de un GAP para el usuario con id = {}", userId);
        return gapRepository.save(gap);
    }

    /**
     * Calcula automáticamente los GAPs de un usuario para una fecha específica,
     * comparando los huecos libres entre sus bloques de clases. Antes de recalcular,
     * borra los GAPs previamente generados para ese día que no tengan un match activo
     * asociado (para no perder GAPs comprometidos en un match y evitar duplicados).
     */
    @Transactional
    public List<GapModel> calculateGapsFromSchedule(Long userId, LocalDate date) {
        log.info("Inicia proceso de calcular GAPs del usuario con id = {} para la fecha {}", userId, date);

        UserModel user = userService.getById(userId);

        // Se eliminan los GAPs previamente calculados para ese día, para no duplicar
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
        List<MatchModel> activeMatches = matchRepository.findActiveMatchesByUserAndDate(
            userId,
            dayStart,
            dayEnd,
            List.of(MatchStatusEnum.WAITING_RESPONSE, MatchStatusEnum.HAPPENING_NOW)
        );
        List<GapModel> gapsPrevios = gapRepository.findByUserIdAndStartTimeBetween(userId, dayStart, dayEnd);
        List<GapModel> gapsSinMatch = gapsPrevios.stream()
            .filter(gap -> activeMatches.stream().noneMatch(match ->
                match.getRequesterGap().getId().equals(gap.getId())
                    || match.getReceiverGap().getId().equals(gap.getId())))
            .toList();
        gapRepository.deleteAll(gapsSinMatch);

        // Se obtienen las clases del usuario para el día de la semana correspondiente
        DayOfWeekEnum dayOfWeek = DayOfWeekEnum.valueOf(date.getDayOfWeek().name().substring(0, 3));
        List<ClassBlockModel> clases = classBlockService.getAllByUser(userId).stream()
                .filter(c -> c.getDayOfWeek() == dayOfWeek)
                .sorted(Comparator.comparing(ClassBlockModel::getStartTime))
                .toList();

        // Si no tiene clases ese día, no se generan GAPs
        if (clases.isEmpty()) {
            log.info("Usuario con id = {} no tiene clases el {}, no se generan GAPs", userId, dayOfWeek);
            return List.of();
        }

        List<GapModel> gaps = new ArrayList<>();
        LocalTime cursor = CAMPUS_OPEN;

        for (ClassBlockModel clase : clases) {
            if (clase.getStartTime().isAfter(cursor)) {
                addGapIfAvailable(gaps, activeMatches, user, date, cursor, clase.getStartTime());
            }
            if (clase.getEndTime().isAfter(cursor)) {
                cursor = clase.getEndTime();
            }
        }

        if (cursor.isBefore(CAMPUS_CLOSE)) {
            addGapIfAvailable(gaps, activeMatches, user, date, cursor, CAMPUS_CLOSE);
        }

        log.info("Termina proceso de calcular GAPs del usuario con id = {} para la fecha {}", userId, date);
        return gapRepository.saveAll(gaps);
    }

    /**
     * Agrega un GAP candidato a la lista solo si el rango de horas no se solapa
     * con ningún match activo del usuario (para no crear un GAP encima de un
     * compromiso ya existente).
     */
    private void addGapIfAvailable(
            List<GapModel> gaps,
            List<MatchModel> activeMatches,
            UserModel user,
            LocalDate date,
            LocalTime start,
            LocalTime end) {
        LocalDateTime gapStart = date.atTime(start);
        LocalDateTime gapEnd = date.atTime(end);
        boolean overlapsMatch = activeMatches.stream().anyMatch(match ->
                gapStart.isBefore(match.getOverlapEnd())
                        && gapEnd.isAfter(match.getOverlapStart()));

        if (!overlapsMatch) {
            gaps.add(buildGap(user, date, start, end));
        }
    }

    /**
     * Construye (sin guardar) una entidad GapModel a partir de una fecha
     * y un rango de horas, calculando su duración en minutos.
     */
    private GapModel buildGap(UserModel user, LocalDate date, LocalTime start, LocalTime end) {
        GapModel gap = new GapModel();
        gap.setUser(user);
        gap.setStartTime(date.atTime(start));
        gap.setEndTime(date.atTime(end));
        gap.setDurationMinutes((int) java.time.Duration.between(start, end).toMinutes());
        return gap;
    }

    /**
     * Valida que un GAP tenga hora de inicio y fin, y que el inicio sea
     * estrictamente anterior al fin.
     */
    private void validateGapData(GapModel gap) {
        if (gap.getStartTime() == null || gap.getEndTime() == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias");
        }
        if (!gap.getStartTime().isBefore(gap.getEndTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser antes de la hora de fin");
        }
    }

    /**
     * Busca si un usuario tiene un GAP que cubra completamente un rango de tiempo
     * específico (usado, por ejemplo, para validar que un usuario esté libre
     * al momento de crear o aceptar un match).
     */
    @Transactional(readOnly = true)
    public Optional<GapModel> getActiveGap(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        return gapRepository
                .findFirstByUserIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeAsc(
                        userId,
                        startTime,
                        endTime
                );
    }

    /**
     * Dado un listado de ids de amigos, devuelve cuáles de ellos tienen
     * un GAP activo en el instante indicado (están "libres" en ese momento).
     */
    @Transactional(readOnly = true)
    public List<UserModel> getAvailableFriends(List<Long> friendIds, LocalDateTime now) {

        if (friendIds == null || friendIds.isEmpty()) {
            return List.of();
        }

        return gapRepository.findAvailableFriends(friendIds, now);
    }

    /**
     * Devuelve todos los GAPs activos en este instante, excluyendo los de un
     * usuario específico. Usado para encontrar candidatos de match para ese usuario.
     */
    @Transactional(readOnly = true)
    public List<GapModel> getActiveGapsExcludingUser(Long excludeUserId, LocalDateTime now) {
        return gapRepository.findActiveGapsExcludingUser(excludeUserId, now);
    }

    /**
     * Igual que getAvailableFriends, pero siempre evaluado contra el instante actual.
     */
    @Transactional(readOnly = true)
    public List<UserModel> getAvailableFriendsNow(List<Long> friendIds) {

        return getAvailableFriends(
                friendIds,
                LocalDateTime.now()
        );
    }

    /**
     * Job automático (cada 60s) que revisa los GAPs de todos los usuarios y dispara
     * una notificación única por cada evento de su ciclo de vida: a punto de empezar,
     * empezado, a punto de terminar, y terminado. Cada tipo de evento usa su propio
     * flag de "ya notificado" en la entidad para no notificar dos veces lo mismo.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkGapNotifications() {
        log.info("Inicia revisión de notificaciones de GAP");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soonLimit = now.plusMinutes(SOON_WINDOW_MINUTES);

        // GAP_STARTING_SOON: empieza dentro de los próximos 20 minutos
        notifyAndMark(
                gapRepository.findByStartTimeBetweenAndStartingSoonNotifiedFalse(now, soonLimit),
                NotificationTypeEnum.GAP_STARTING_SOON,
                "Tu GAP está por comenzar en breve",
                GapModel::setStartingSoonNotified
        );

        // GAP_STARTED: ya comenzó
        notifyAndMark(
                gapRepository.findByStartTimeLessThanEqualAndStartedNotifiedFalse(now),
                NotificationTypeEnum.GAP_STARTED,
                "Tu GAP ha comenzado",
                GapModel::setStartedNotified
        );

        // GAP_ENDING_SOON: termina dentro de los próximos 20 minutos
        notifyAndMark(
                gapRepository.findByEndTimeBetweenAndEndingSoonNotifiedFalse(now, soonLimit),
                NotificationTypeEnum.GAP_ENDING_SOON,
                "Tu GAP está por terminar",
                GapModel::setEndingSoonNotified
        );

        // GAP_ENDED: ya terminó
        notifyAndMark(
                gapRepository.findByEndTimeLessThanEqualAndEndedNotifiedFalse(now),
                NotificationTypeEnum.GAP_ENDED,
                "Tu GAP ha terminado",
                GapModel::setEndedNotified
        );

        log.info("Termina revisión de notificaciones de GAP");
    }

    /**
     * Helper reutilizado por checkGapNotifications: por cada GAP de la lista,
     * publica el evento de notificación indicado y marca su flag correspondiente
     * como notificado, para luego persistir todos los cambios de una sola vez.
     */
    private void notifyAndMark(
            List<GapModel> gaps,
            NotificationTypeEnum type,
            String message,
            BiConsumer<GapModel, Boolean> markAsNotified) {

        for (GapModel gap : gaps) {
            notificationPublisher.publish(new NotificationEvent(
                    gap.getUser().getId(),
                    type,
                    gap.getId(),
                    message
            ));
            markAsNotified.accept(gap, true);
        }
        gapRepository.saveAll(gaps);
    }

        /**
     * Devuelve únicamente los GAPs pasados del usuario
     * (aquellos cuya hora de fin es anterior o igual a la hora actual).
     */
    @Transactional(readOnly = true)
    public List<GapModel> getPastByUser(Long userId) {
        log.info("Inicia proceso de consultar los GAPs pasados del usuario con id = {}", userId);

        userService.getById(userId);

        return gapRepository.findByUserIdAndEndTimeLessThanEqualOrderByStartTimeDesc(
                userId, 
                LocalDateTime.now()
        );
}
}