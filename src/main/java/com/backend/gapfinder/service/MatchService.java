package com.backend.gapfinder.service;

import com.backend.gapfinder.dto.response.InterestBasicDTO;
import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationPublisher;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.model.GapModel;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.model.MatchModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.MatchRepository;
import com.backend.gapfinder.strategies.CompatibilityStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MatchService {

    private static final List<MatchStatusEnum> ACTIVE_STATUSES =
            List.of(MatchStatusEnum.WAITING_RESPONSE, MatchStatusEnum.HAPPENING_NOW);

    private static final double INTERESTS_WEIGHT = 0.8;
    private static final double PRIORITY_WEIGHT = 0.2;

    private final MatchRepository matchRepository;
    private final UserService userService;
    private final GapService gapService;
    private final ActivityService activityService;
    private final NotificationPublisher notificationPublisher;
    private final Map<MatchModeEnum, CompatibilityStrategy> strategies;

    public MatchService(
            MatchRepository matchRepository,
            UserService userService,
            GapService gapService,
            ActivityService activityService,
            NotificationPublisher notificationPublisher,
            List<CompatibilityStrategy> compatibilityStrategies) {
        this.matchRepository = matchRepository;
        this.userService = userService;
        this.gapService = gapService;
        this.activityService = activityService;
        this.notificationPublisher = notificationPublisher;
        this.strategies = compatibilityStrategies.stream()
            .collect(Collectors.toMap(CompatibilityStrategy::getMode, strategy -> strategy));
    }

    @Transactional(readOnly = true)
    public List<MatchCandidate> findMatchCandidates(Long userId, MatchModeEnum mode) {
        log.info("Inicia proceso de buscar candidatos de match para el usuario con id = {} en modo {}", userId, mode);

        UserModel me = userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();
        getCurrentActiveGap(userId, now);

        getStrategy(mode);

        Set<Long> excludedIds = new HashSet<>(matchRepository.findActivePartnerIds(userId, ACTIVE_STATUSES));

        List<MatchCandidate> candidates = gapService.getActiveGapsExcludingUser(userId, now).stream()
                .filter(gap -> !excludedIds.contains(gap.getUser().getId()))
                .filter(gap -> isEffortEligible(me, gap.getUser()))
            .map(gap -> {
                double score = calculateTotalScore(me, gap.getUser(), mode);
                return new MatchCandidate(
                        gap.getUser(),
                        gap,
                        score,
                        findCommonInterests(me, gap.getUser()));
            })
                .sorted(Comparator.comparingDouble(MatchCandidate::compatibility).reversed())
                .toList();

        log.info("Termina proceso de buscar candidatos de match para el usuario con id = {}", userId);
        return candidates;
    }

    private boolean isEffortEligible(UserModel me, UserModel candidate) {
        if (me.getActivityEffortPreference() == null || candidate.getActivityEffortPreference() == null) {
            return true;
        }
        return candidate.getActivityEffortPreference().ordinal() <= me.getActivityEffortPreference().ordinal();
    }

    private double calculateInterestsScore(UserModel userA, UserModel userB) {
        Set<Long> interestsA = toInterestIds(userA);
        Set<Long> interestsB = toInterestIds(userB);

        if (interestsA.isEmpty() && interestsB.isEmpty()) {
            return 0.0;
        }

        Set<Long> intersection = new HashSet<>(interestsA);
        intersection.retainAll(interestsB);

        Set<Long> union = new HashSet<>(interestsA);
        union.addAll(interestsB);

        return (double) intersection.size() / union.size();
    }

    private double calculateTotalScore(UserModel userA, UserModel userB, MatchModeEnum mode) {
        double interestsScore = calculateInterestsScore(userA, userB);
        double priorityBonus = getStrategy(mode).calculate(userA, userB);

        double total = (interestsScore * INTERESTS_WEIGHT) + (priorityBonus * PRIORITY_WEIGHT);
        return Math.round(total * 100.0) / 100.0;
    }

    private List<InterestBasicDTO> findCommonInterests(UserModel userA, UserModel userB) {
        Set<Long> userBInterestIds = toInterestIds(userB);

        return userA.getInterests().stream()
                .filter(interest -> userBInterestIds.contains(interest.getId()))
                .map(this::toInterestBasicDTO)
                .toList();
    }

    private InterestBasicDTO toInterestBasicDTO(InterestModel interest) {
        InterestBasicDTO dto = new InterestBasicDTO();
        dto.setId(interest.getId());
        dto.setName(interest.getName());
        return dto;
    }

    @Transactional
    public MatchModel sendRequest(Long requesterId, Long receiverId) {
        log.info("Inicia proceso de envío de match de {} hacia {}", requesterId, receiverId);

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Un usuario no puede enviarse un match a sí mismo");
        }

        UserModel requester = userService.getById(requesterId);
        UserModel receiver = userService.getById(receiverId);

        matchRepository.findActiveBetweenUsers(requesterId, receiverId, ACTIVE_STATUSES)
                .ifPresent(m -> {
                    throw new IllegalStateException("Ya existe un match activo entre estos dos usuarios");
                });

        LocalDateTime now = LocalDateTime.now();

        GapModel requesterGap = getCurrentActiveGap(requesterId, now);
        GapModel receiverGap = getCurrentActiveGap(receiverId, now);

        LocalDateTime overlapStart = requesterGap.getStartTime().isAfter(receiverGap.getStartTime())
                ? requesterGap.getStartTime() : receiverGap.getStartTime();
        LocalDateTime overlapEnd = requesterGap.getEndTime().isBefore(receiverGap.getEndTime())
                ? requesterGap.getEndTime() : receiverGap.getEndTime();

        if (!overlapStart.isBefore(overlapEnd)) {
            throw new IllegalStateException("El candidato ya no tiene un GAP que se cruce con el tuyo");
        }

        MatchModel match = new MatchModel();
        match.setRequester(requester);
        match.setReceiver(receiver);
        match.setRequesterGap(requesterGap);
        match.setReceiverGap(receiverGap);
        match.setOverlapStart(overlapStart);
        match.setOverlapEnd(overlapEnd);
        match.setStatus(MatchStatusEnum.WAITING_RESPONSE);
        match.setCreatedAt(now);

        MatchModel saved = matchRepository.save(match);
        notificationPublisher.publish(new NotificationEvent(
            receiverId,
            NotificationTypeEnum.MATCH_REQUEST,
            saved.getId(),
            requester.getName() + " te envió una solicitud de match"
        ));

        log.info("Termina proceso de envío de match de {} hacia {}", requesterId, receiverId);
        return saved;
    }

    // Al aceptar, se sugiere y asigna automáticamente la actividad del match
    @Transactional
    public MatchModel acceptRequest(Long matchId, Long receiverId) {
        log.info("Inicia proceso de aceptar match con id = {}", matchId);

        MatchModel match = getById(matchId);

        if (!match.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Solo el receptor del match puede aceptarlo");
        }
        requireWaitingResponse(match);

        match.setStatus(MatchStatusEnum.HAPPENING_NOW);
        match.setChosenActivity(suggestActivity(match));

        MatchModel saved = matchRepository.save(match);
        notificationPublisher.publish(new NotificationEvent(
            saved.getRequester().getId(),
            NotificationTypeEnum.MATCH_ACCEPTED,
            saved.getId(),
            saved.getReceiver().getName() + " aceptó tu solicitud de match"
        ));

        log.info("Termina proceso de aceptar match con id = {}", matchId);
        return saved;
    }

    @Transactional
    public MatchModel rejectRequest(Long matchId, Long receiverId) {
        log.info("Inicia proceso de rechazar match con id = {}", matchId);

        MatchModel match = getById(matchId);

        if (!match.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Solo el receptor del match puede rechazarlo");
        }
        requireWaitingResponse(match);

        match.setStatus(MatchStatusEnum.REJECTED);

        MatchModel saved = matchRepository.save(match);
        notificationPublisher.publish(new NotificationEvent(
            saved.getRequester().getId(),
            NotificationTypeEnum.MATCH_REJECTED,
            saved.getId(),
            saved.getReceiver().getName() + " rechazó tu solicitud de match"
        ));

        log.info("Termina proceso de rechazar match con id = {}", matchId);
        return saved;
    }

    @Transactional
    public MatchModel getById(Long id) {
        log.info("Inicia proceso de consultar el match con id = {}", id);
        MatchModel match = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El match con id " + id + " no existe"));
        closeIfExpired(match);
        return match;
    }

    // Si el match sigue activo pero su tiempo de solapamiento ya pasó, lo cierra
    // y notifica a ambos usuarios que su match terminó.
    private void closeIfExpired(MatchModel match) {
        if (ACTIVE_STATUSES.contains(match.getStatus())
                && !match.getOverlapEnd().isAfter(LocalDateTime.now())) {
            match.setStatus(MatchStatusEnum.CLOSED);
            MatchModel saved = matchRepository.save(match);
            notifyMatchClosed(saved);
        }
    }

    @Transactional
    public List<MatchModel> getHistoryByUser(Long userId) {
        log.info("Inicia proceso de consultar historial de matches del usuario con id = {}", userId);

        userService.getById(userId);

        return matchRepository.findHistoryByUser(userId);
    }

    @Transactional(readOnly = true)
    public double calculateCompatibility(Long userAId, Long userBId, MatchModeEnum mode) {
        UserModel userA = userService.getById(userAId);
        UserModel userB = userService.getById(userBId);

        return calculateTotalScore(userA, userB, mode);
    }

    private CompatibilityStrategy getStrategy(MatchModeEnum mode) {
        CompatibilityStrategy strategy = strategies.get(mode);
        if (strategy == null) {
            throw new IllegalArgumentException("Modo de match no soportado: " + mode);
        }
        return strategy;
    }

    private GapModel getCurrentActiveGap(Long userId, LocalDateTime now) {
        return gapService.getActiveGap(userId, now, now)
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario con id " + userId + " no tiene un GAP activo en este momento"));
    }

    private Set<Long> toInterestIds(UserModel user) {
        Set<Long> ids = new HashSet<>();
        for (InterestModel interest : user.getInterests()) {
            ids.add(interest.getId());
        }
        return ids;
    }

    private void requireWaitingResponse(MatchModel match) {
        if (match.getStatus() != MatchStatusEnum.WAITING_RESPONSE) {
            throw new IllegalStateException(
                    "Esta acción solo es válida cuando el match está WAITING_RESPONSE (estado actual: "
                            + match.getStatus() + ")"
            );
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void closeExpiredMatches() {
        List<MatchModel> expiredMatches = matchRepository.findByStatusInAndOverlapEndBefore(
                ACTIVE_STATUSES, LocalDateTime.now());
        expiredMatches.forEach(match -> match.setStatus(MatchStatusEnum.CLOSED));
        if (!expiredMatches.isEmpty()) {
            List<MatchModel> saved = matchRepository.saveAll(expiredMatches);
            saved.forEach(this::notifyMatchClosed);
        }
    }

    // Notifica a requester y receiver que su match terminó
    private void notifyMatchClosed(MatchModel match) {
        notificationPublisher.publish(new NotificationEvent(
                match.getRequester().getId(),
                NotificationTypeEnum.MATCH_CLOSED,
                match.getId(),
                "Tu match con " + match.getReceiver().getName() + " ha terminado"
        ));
        notificationPublisher.publish(new NotificationEvent(
                match.getReceiver().getId(),
                NotificationTypeEnum.MATCH_CLOSED,
                match.getId(),
                "Tu match con " + match.getRequester().getName() + " ha terminado"
        ));
    }

    public record MatchCandidate(
        UserModel user,
        GapModel activeGap,
        double compatibility,
        List<InterestBasicDTO> commonInterests) {}

    // Elige automáticamente la actividad del match: cabe en el tiempo de
    // solapamiento, respeta el esfuerzo mínimo entre los dos, y si hay
    // intereses en común, debe tener alguno de esos intereses
    private ActivityModel suggestActivity(MatchModel match) {
        List<ActivityModel> eligible = getEligibleActivities(
                match.getRequester(), match.getReceiver(), match.getOverlapStart(), match.getOverlapEnd());

        if (eligible.isEmpty()) {
            throw new IllegalStateException("No hay actividades disponibles para sugerir en este match");
        }

        return eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
    }

    // Actividades sugeridas para un match ya existente, según su tiempo de
    // solapamiento real (para mostrarlas en pantalla antes o después de aceptar)
    @Transactional(readOnly = true)
    public List<ActivityModel> getSuggestedActivities(Long matchId) {
        log.info("Buscando actividades sugeridas para el match con id = {}", matchId);

        MatchModel match = getById(matchId);
        return getEligibleActivities(
                match.getRequester(), match.getReceiver(), match.getOverlapStart(), match.getOverlapEnd());
    }

    private List<ActivityModel> getEligibleActivities(
            UserModel userA, UserModel userB, LocalDateTime overlapStart, LocalDateTime overlapEnd) {

        int availableMinutes = (int) Duration.between(overlapStart, overlapEnd).toMinutes();

        Set<Long> interestsA = toInterestIds(userA);
        Set<Long> interestsB = toInterestIds(userB);
        Set<Long> commonInterests = new HashSet<>(interestsA);
        commonInterests.retainAll(interestsB);

        ActivityEffortEnum requiredEffort = resolveRequiredEffort(
                userA.getActivityEffortPreference(), userB.getActivityEffortPreference());

        return activityService.findEligibleActivities(availableMinutes, requiredEffort, commonInterests);
    }

    private ActivityEffortEnum resolveRequiredEffort(ActivityEffortEnum a, ActivityEffortEnum b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    @Transactional
    public MatchModel setRematchPreference(Long matchId, Long userId, boolean wantsRematch) {
        log.info("Registrando preferencia de rematch del usuario {} en el match {}", userId, matchId);

        MatchModel match = getById(matchId);

        if (match.getStatus() != MatchStatusEnum.CLOSED) {
            throw new IllegalStateException("Solo se puede calificar un match cerrado");
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