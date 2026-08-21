package com.backend.gapfinder.entities.match;

import com.backend.gapfinder.entities.activity.ActivityEntity;
import com.backend.gapfinder.entities.activity.ActivityService;
import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.gap.GapService;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MatchService {

    private static final List<MatchStatusEnum> ACTIVE_STATUSES =
            List.of(MatchStatusEnum.PENDING, MatchStatusEnum.ACCEPTED);

    private final MatchRepository matchRepository;
    private final UserService userService;
    private final GapService gapService;
    private final ActivityService activityService;

    public MatchService(
            MatchRepository matchRepository,
            UserService userService,
            GapService gapService,
            ActivityService activityService) {
        this.matchRepository = matchRepository;
        this.userService = userService;
        this.gapService = gapService;
        this.activityService = activityService;
    }

    // Buscar candidatos de match para un usuario: cualquier usuario de la app con
    // un GAP activo ahora mismo, sin match PENDING/ACCEPTED vigente con él,
    // ordenados de mayor a menor compatibilidad
    @Transactional(readOnly = true)
    public List<MatchCandidate> findMatchCandidates(Long userId) {
        log.info("Inicia proceso de buscar candidatos de match para el usuario con id = {}", userId);

        UserEntity me = userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();

        getCurrentActiveGap(userId, now);

        Set<Long> excludedIds = new HashSet<>(matchRepository.findActivePartnerIds(userId, ACTIVE_STATUSES));

        List<MatchCandidate> candidates = gapService.getActiveGapsExcludingUser(userId, now).stream()
                .filter(gap -> !excludedIds.contains(gap.getUser().getId()))
                .map(gap -> new MatchCandidate(gap.getUser(), gap, calculateCompatibility(me, gap.getUser())))
                .sorted(Comparator.comparingDouble(MatchCandidate::compatibility).reversed())
                .toList();

        log.info("Termina proceso de buscar candidatos de match para el usuario con id = {}", userId);
        return candidates;
    }

    // Proponer un match: el requester elige a un candidato de findMatchCandidates y le envía la solicitud
    @Transactional
    public MatchEntity sendRequest(Long requesterId, Long receiverId) {
        log.info("Inicia proceso de envío de match de {} hacia {}", requesterId, receiverId);

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Un usuario no puede enviarse un match a sí mismo");
        }

        UserEntity requester = userService.getById(requesterId);
        UserEntity receiver = userService.getById(receiverId);

        matchRepository.findActiveBetweenUsers(requesterId, receiverId, ACTIVE_STATUSES)
                .ifPresent(m -> {
                    throw new IllegalStateException("Ya existe un match activo entre estos dos usuarios");
                });

        LocalDateTime now = LocalDateTime.now();

        // Se recalcula el GAP activo de cada uno en este instante, en vez de confiar
        // en un gapId enviado desde el cliente (evita proponer un match sobre un GAP viejo)
        GapEntity requesterGap = getCurrentActiveGap(requesterId, now);
        GapEntity receiverGap = getCurrentActiveGap(receiverId, now);

        LocalDateTime overlapStart = requesterGap.getStartTime().isAfter(receiverGap.getStartTime())
                ? requesterGap.getStartTime() : receiverGap.getStartTime();
        LocalDateTime overlapEnd = requesterGap.getEndTime().isBefore(receiverGap.getEndTime())
                ? requesterGap.getEndTime() : receiverGap.getEndTime();

        if (!overlapStart.isBefore(overlapEnd)) {
            throw new IllegalStateException("El candidato ya no tiene un GAP que se cruce con el tuyo");
        }

        MatchEntity match = new MatchEntity();
        match.setRequester(requester);
        match.setReceiver(receiver);
        match.setRequesterGap(requesterGap);
        match.setReceiverGap(receiverGap);
        match.setOverlapStart(overlapStart);
        match.setOverlapEnd(overlapEnd);
        match.setStatus(MatchStatusEnum.PENDING);
        match.setCreatedAt(now);

        log.info("Termina proceso de envío de match de {} hacia {}", requesterId, receiverId);
        return matchRepository.save(match);
    }

    @Transactional
    public MatchEntity acceptRequest(Long matchId, Long receiverId) {
        log.info("Inicia proceso de aceptar match con id = {}", matchId);

        MatchEntity match = getById(matchId);

        if (!match.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Solo el receptor del match puede aceptarlo");
        }
        requirePending(match);

        match.setStatus(MatchStatusEnum.ACCEPTED);

        log.info("Termina proceso de aceptar match con id = {}", matchId);
        return matchRepository.save(match);
    }

    @Transactional
    public MatchEntity rejectRequest(Long matchId, Long receiverId) {
        log.info("Inicia proceso de rechazar match con id = {}", matchId);

        MatchEntity match = getById(matchId);

        if (!match.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Solo el receptor del match puede rechazarlo");
        }
        requirePending(match);

        match.setStatus(MatchStatusEnum.REJECTED);

        log.info("Termina proceso de rechazar match con id = {}", matchId);
        return matchRepository.save(match);
    }

    @Transactional
    public MatchEntity cancelRequest(Long matchId, Long requesterId) {
        log.info("Inicia proceso de cancelar match con id = {}", matchId);

        MatchEntity match = getById(matchId);

        if (!match.getRequester().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Solo quien envió la solicitud puede cancelarla");
        }
        requirePending(match);

        match.setStatus(MatchStatusEnum.CANCELLED);

        log.info("Termina proceso de cancelar match con id = {}", matchId);
        return matchRepository.save(match);
    }

    @Transactional
    public MatchEntity getById(Long id) {
        log.info("Inicia proceso de consultar el match con id = {}", id);
        return matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El match con id " + id + " no existe"));
    }

    @Transactional
    public List<MatchEntity> getHistoryByUser(Long userId) {
        log.info("Inicia proceso de consultar historial de matches del usuario con id = {}", userId);

        userService.getById(userId);

        return matchRepository.findHistoryByUser(userId);
    }

    // Guardar la propuesta de actividad de cualquiera de los dos participantes
    @Transactional
    public MatchEntity chooseActivity(Long matchId, Long userId, Long activityId) {
        log.info("Usuario {} elige actividad para el match con id = {}", userId, matchId);

        MatchEntity match = getById(matchId);

        if (match.getStatus() != MatchStatusEnum.ACCEPTED) {
            throw new IllegalStateException("Solo se puede elegir actividad si el match fue aceptado");
        }
        if (match.getChosenActivity() != null) {
            throw new IllegalStateException("La actividad final del match ya fue sorteada");
        }

        boolean isRequester = match.getRequester().getId().equals(userId);
        boolean isReceiver = match.getReceiver().getId().equals(userId);

        if (!isRequester && !isReceiver) {
            throw new IllegalArgumentException("Solo los participantes del match pueden elegir actividad");
        }

        ActivityEntity activity = activityService.getById(activityId);

        if (isRequester) {
            match.setRequesterChosenActivity(activity);
        } else {
            match.setReceiverChosenActivity(activity);
        }

        if (match.getRequesterChosenActivity() != null && match.getReceiverChosenActivity() != null) {
            ActivityEntity winner = ThreadLocalRandom.current().nextBoolean()
                    ? match.getRequesterChosenActivity()
                    : match.getReceiverChosenActivity();
            match.setChosenActivity(winner);
            log.info("Ambos eligieron, se sortea la actividad final: {}", winner.getTitle());
        }

        log.info("Termina proceso de elegir actividad para el match con id = {}", matchId);
        return matchRepository.save(match);
    }

    // Compatibilidad entre dos usuarios: Jaccard sobre intereses en común + bonus por preferencia de esfuerzo igual
    @Transactional(readOnly = true)
    public double calculateCompatibility(Long userAId, Long userBId) {
        UserEntity userA = userService.getById(userAId);
        UserEntity userB = userService.getById(userBId);
        return calculateCompatibility(userA, userB);
    }

    private double calculateCompatibility(UserEntity userA, UserEntity userB) {
        Set<Long> interestsA = toInterestIds(userA);
        Set<Long> interestsB = toInterestIds(userB);

        double interestScore;
        if (interestsA.isEmpty() && interestsB.isEmpty()) {
            interestScore = 0.0;
        } else {
            Set<Long> intersection = new HashSet<>(interestsA);
            intersection.retainAll(interestsB);

            Set<Long> union = new HashSet<>(interestsA);
            union.addAll(interestsB);

            interestScore = (double) intersection.size() / union.size();
        }

        boolean sameEffortPreference = userA.getActivityEffortPreference() != null
                && userA.getActivityEffortPreference() == userB.getActivityEffortPreference();

        double score = (interestScore * 0.8) + (sameEffortPreference ? 0.2 : 0.0);
        return Math.round(score * 100.0) / 100.0;
    }

    // Buscar el GAP activo ahora mismo de un usuario; falla si no tiene ninguno
    private GapEntity getCurrentActiveGap(Long userId, LocalDateTime now) {
        return gapService.getActiveGap(userId, now, now)
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario con id " + userId + " no tiene un GAP activo en este momento"));
    }

    private Set<Long> toInterestIds(UserEntity user) {
        Set<Long> ids = new HashSet<>();
        for (InterestEntity interest : user.getInterests()) {
            ids.add(interest.getId());
        }
        return ids;
    }

    private void requirePending(MatchEntity match) {
        if (match.getStatus() != MatchStatusEnum.PENDING) {
            throw new IllegalStateException(
                    "Esta acción solo es válida cuando el match está PENDING (estado actual: " + match.getStatus() + ")"
            );
        }
    }

    // Candidato de match: el usuario, su GAP activo actual, y el score de compatibilidad (0.0–1.0)
    public record MatchCandidate(UserEntity user, GapEntity activeGap, double compatibility) {}

    // Buscar actividades sugeridas según intereses comunes, tiempo disponible
    // y el nivel de esfuerzo que aceptan los dos participantes
    @Transactional(readOnly = true)
    public List<ActivityEntity> getSuggestedActivities(Long matchId, Integer availableMinutes) {
        log.info("Buscando actividades sugeridas para el match con id = {}", matchId);

        if (availableMinutes == null || availableMinutes <= 0) {
            throw new IllegalArgumentException("El tiempo disponible debe ser mayor a 0 minutos");
        }

        MatchEntity match = getById(matchId);

        Set<Long> interestsA = toInterestIds(match.getRequester());
        Set<Long> interestsB = toInterestIds(match.getReceiver());

        Set<Long> commonInterests = new HashSet<>(interestsA);
        commonInterests.retainAll(interestsB);

        ActivityEffortEnum requiredEffort = resolveRequiredEffort(
                match.getRequester().getActivityEffortPreference(),
                match.getReceiver().getActivityEffortPreference());

        return activityService.getAll().stream()
            .filter(activity -> fitsAvailableTime(activity, availableMinutes))
            .filter(activity -> matchesCommonInterest(activity, commonInterests))
            .filter(activity -> matchesEffortLevel(activity, requiredEffort))
                .toList();
    }

    // Gana la preferencia mas restrictiva de los dos: si a uno le sirve algo
    // QUIET no se proponen actividades mas exigentes. Un usuario sin
    // preferencia no restringe, y si ninguno tiene no se filtra por esfuerzo.
    private ActivityEffortEnum resolveRequiredEffort(ActivityEffortEnum a, ActivityEffortEnum b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    // Una actividad sin nivel de esfuerzo definido no se propone mientras haya
    // una restriccion activa: no hay como saber si respeta el limite
    private boolean matchesEffortLevel(ActivityEntity activity, ActivityEffortEnum requiredEffort) {
        return requiredEffort == null
            || activity.getActivityEffortLevel() == requiredEffort;
    }

        private boolean fitsAvailableTime(ActivityEntity activity, Integer availableMinutes) {
        return activity.getDurationMinutes() <= availableMinutes;
        }

        private boolean matchesCommonInterest(ActivityEntity activity, Set<Long> commonInterests) {
        return activity.getInterest() == null
            || commonInterests.contains(activity.getInterest().getId());
        }


    // Registrar si un usuario del match repetiría su GAP con la otra persona,
    // solo permitido si el match fue aceptado y el encuentro ya pasó
    @Transactional
    public MatchEntity setRematchPreference(Long matchId, Long userId, boolean wantsRematch) {
        log.info("Registrando preferencia de rematch del usuario {} en el match {}", userId, matchId);

        MatchEntity match = getById(matchId);

        if (match.getStatus() != MatchStatusEnum.ACCEPTED) {
            throw new IllegalStateException("Solo se puede calificar un match que fue aceptado");
        }

        if (match.getOverlapEnd().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Solo se puede calificar un match una vez que el encuentro ya terminó");
        }

        if (match.getRequester().getId().equals(userId)) {
            match.setRequesterWantsRematch(wantsRematch);
        } else if (match.getReceiver().getId().equals(userId)) {
            match.setReceiverWantsRematch(wantsRematch);
        } else {
            throw new IllegalArgumentException("El usuario no pertenece a este match");
        }

        return matchRepository.save(match);
    }
}