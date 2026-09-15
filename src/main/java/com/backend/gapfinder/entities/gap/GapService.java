package com.backend.gapfinder.entities.gap;

import com.backend.gapfinder.entities.classblock.ClassBlockEntity;
import com.backend.gapfinder.entities.classblock.ClassBlockService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.DayOfWeekEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GapService {

    // Ventana de operación del campus
    private static final LocalTime CAMPUS_OPEN = LocalTime.of(6, 30);
    private static final LocalTime CAMPUS_CLOSE = LocalTime.of(21, 30);

    private final GapRepository gapRepository;
    private final UserService userService;
    private final ClassBlockService classBlockService;

    public GapService(GapRepository gapRepository, UserService userService, ClassBlockService classBlockService) {
        this.gapRepository = gapRepository;
        this.userService = userService;
        this.classBlockService = classBlockService;
    }

    // Consultar un GAP por id
    @Transactional
    public GapEntity getById(Long id) {
        log.info("Inicia proceso de consultar el GAP con id = {}", id);
        return gapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El GAP con id " + id + " no existe"));
    }

    // Consultar el historial completo de GAPs de un usuario
    @Transactional
    public List<GapEntity> getAllByUser(Long userId) {
        log.info("Inicia proceso de consultar el historial de GAPs del usuario con id = {}", userId);

        userService.getById(userId);

        return gapRepository.findByUserId(userId);
    }

    // Consultar los GAPs de hoy de un usuario
    @Transactional
    public List<GapEntity> getTodayByUser(Long userId) {
        log.info("Inicia proceso de consultar los GAPs de hoy del usuario con id = {}", userId);

        userService.getById(userId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return gapRepository.findByUserIdAndStartTimeBetween(userId, start, end);
    }

    // Guardar un GAP manualmente
    @Transactional
    public GapEntity create(Long userId, GapEntity gap) {
        log.info("Inicia proceso de creación de un GAP para el usuario con id = {}", userId);

        validateGapData(gap);

        UserEntity user = userService.getById(userId);
        gap.setId(null);
        gap.setUser(user);
        gap.setDurationMinutes((int) java.time.Duration.between(gap.getStartTime(), gap.getEndTime()).toMinutes());

        log.info("Termina proceso de creación de un GAP para el usuario con id = {}", userId);
        return gapRepository.save(gap);
    }

    // Calcular los GAPs de un día comparando las clases del usuario y guardarlos
    @Transactional
    public List<GapEntity> calculateGapsFromSchedule(Long userId, LocalDate date) {
        log.info("Inicia proceso de calcular GAPs del usuario con id = {} para la fecha {}", userId, date);

        UserEntity user = userService.getById(userId);

        // Se eliminan los GAPs previamente calculados para ese día, para no duplicar
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
        List<GapEntity> gapsPrevios = gapRepository.findByUserIdAndStartTimeBetween(userId, dayStart, dayEnd);
        gapRepository.deleteAll(gapsPrevios);

        // Se obtienen las clases del usuario para el día de la semana correspondiente
        DayOfWeekEnum dayOfWeek = DayOfWeekEnum.valueOf(date.getDayOfWeek().name().substring(0, 3));
        List<ClassBlockEntity> clases = classBlockService.getAllByUser(userId).stream()
                .filter(c -> c.getDayOfWeek() == dayOfWeek)
                .sorted(Comparator.comparing(ClassBlockEntity::getStartTime))
                .toList();

        // Si no tiene clases ese día, no se generan GAPs
        if (clases.isEmpty()) {
            log.info("Usuario con id = {} no tiene clases el {}, no se generan GAPs", userId, dayOfWeek);
            return List.of();
        }

        List<GapEntity> gaps = new ArrayList<>();
        LocalTime cursor = CAMPUS_OPEN;

        for (ClassBlockEntity clase : clases) {
            if (clase.getStartTime().isAfter(cursor)) {
                gaps.add(buildGap(user, date, cursor, clase.getStartTime()));
            }
            if (clase.getEndTime().isAfter(cursor)) {
                cursor = clase.getEndTime();
            }
        }

        if (cursor.isBefore(CAMPUS_CLOSE)) {
            gaps.add(buildGap(user, date, cursor, CAMPUS_CLOSE));
        }

        log.info("Termina proceso de calcular GAPs del usuario con id = {} para la fecha {}", userId, date);
        return gapRepository.saveAll(gaps);
    }

    // Construir un GAP a partir de una fecha y un rango de horas
    private GapEntity buildGap(UserEntity user, LocalDate date, LocalTime start, LocalTime end) {
        GapEntity gap = new GapEntity();
        gap.setUser(user);
        gap.setStartTime(date.atTime(start));
        gap.setEndTime(date.atTime(end));
        gap.setDurationMinutes((int) java.time.Duration.between(start, end).toMinutes());
        return gap;
    }

    // Validar que los datos del GAP sean correctos
    private void validateGapData(GapEntity gap) {
        if (gap.getStartTime() == null || gap.getEndTime() == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias");
        }
        if (!gap.getStartTime().isBefore(gap.getEndTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser antes de la hora de fin");
        }
    }

    // Buscar si un usuario tiene un GAP que cubra un rango de tiempo específico
    @Transactional(readOnly = true)
    public Optional<GapEntity> getActiveGap(
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


    // Buscar qué amigos están libres en una fecha y hora específica
    @Transactional(readOnly = true)
    public List<UserEntity> getAvailableFriends(List<Long> friendIds, LocalDateTime now) {

        if (friendIds == null || friendIds.isEmpty()) {
            return List.of();
        }

        return gapRepository.findAvailableFriends(friendIds, now);
    }

    @Transactional(readOnly = true)
    public List<GapEntity> getActiveGapsExcludingUser(Long excludeUserId, LocalDateTime now) {
        return gapRepository.findActiveGapsExcludingUser(excludeUserId, now);
    }

    // Buscar qué amigos están libres en este momento
    @Transactional(readOnly = true)
    public List<UserEntity> getAvailableFriendsNow(List<Long> friendIds) {

        return getAvailableFriends(
                friendIds,
                LocalDateTime.now()
        );
    }
}