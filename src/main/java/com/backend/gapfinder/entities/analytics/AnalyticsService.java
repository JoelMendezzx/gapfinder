package com.backend.gapfinder.entities.analytics;

import com.backend.gapfinder.entities.gap.GapRepository;
import com.backend.gapfinder.entities.match.MatchRepository;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantRepository;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.enums.VisibilityScopeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consultas agregadas que responden las Business Questions del proyecto.
 *
 * Vive aparte de los servicios de dominio a proposito: son preguntas de
 * analitica que cruzan varias entidades (GAPs con matches, matches con
 * Open Tables) y no le pertenecen a ninguna sola.
 */
@Slf4j
@Service
public class AnalyticsService {

    private final GapRepository gapRepository;
    private final MatchRepository matchRepository;
    private final OpenTableParticipantRepository participantRepository;

    public AnalyticsService(GapRepository gapRepository,
                            MatchRepository matchRepository,
                            OpenTableParticipantRepository participantRepository) {
        this.gapRepository = gapRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * BQ 5: que duraciones de GAP terminan mas seguido sin match.
     * Ordena de mayor a menor tasa sin match, que es lo que responde la
     * pregunta de un vistazo.
     */
    @Transactional(readOnly = true)
    public List<GapDurationOutcomeDTO> gapDurationsWithoutMatch() {
        log.info("BQ 5: calculando duraciones de GAP que terminan sin match");

        Map<Integer, Long> matched = new LinkedHashMap<>();
        for (Object[] row : gapRepository.countMatchedGapsByDuration(MatchStatusEnum.ACCEPTED)) {
            matched.put((Integer) row[0], (Long) row[1]);
        }

        List<GapDurationOutcomeDTO> result = new ArrayList<>();
        for (Object[] row : gapRepository.countGapsByDuration()) {
            int duration = (Integer) row[0];
            long published = (Long) row[1];
            long withMatch = matched.getOrDefault(duration, 0L);
            long without = published - withMatch;

            result.add(new GapDurationOutcomeDTO(
                    duration, published, withMatch, without, rate(without, published)));
        }

        result.sort((a, b) -> Double.compare(b.getWithoutMatchRate(), a.getWithoutMatchRate()));
        return result;
    }

    /**
     * BQ 7: que mecanismo logra mas encuentros aceptados, los planes
     * abiertos (Open Tables) o las invitaciones directas (matches).
     */
    @Transactional(readOnly = true)
    public List<ConnectionMechanismDTO> connectionMechanisms() {
        log.info("BQ 7: comparando planes abiertos contra invitaciones directas");

        long directAccepted = 0, directRejected = 0, directPending = 0;
        for (Object[] row : matchRepository.countByStatus()) {
            MatchStatusEnum status = (MatchStatusEnum) row[0];
            long count = (Long) row[1];
            switch (status) {
                case ACCEPTED -> directAccepted = count;
                // Un match cancelado por quien lo envio no dice nada sobre
                // si el mecanismo convence: no cuenta como rechazo.
                case REJECTED -> directRejected = count;
                case PENDING -> directPending = count;
                default -> { }
            }
        }

        long openAccepted = 0, openRejected = 0, openPending = 0;
        for (Object[] row : participantRepository.countByRsvpExcludingCreator()) {
            ResponseStatusEnum rsvp = (ResponseStatusEnum) row[0];
            long count = (Long) row[1];
            switch (rsvp) {
                case IN -> openAccepted = count;
                case OUT -> openRejected = count;
                case PENDING -> openPending = count;
            }
        }

        return List.of(
                mechanism("OPEN_PLAN", openAccepted, openRejected, openPending),
                mechanism("DIRECT_INVITATION", directAccepted, directRejected, directPending));
    }

    /**
     * BQ 9: que opcion de visibilidad genera mas conexiones aceptadas.
     *
     * Se apoya en la visibilidad congelada en cada GAP al publicarlo, no en
     * la configuracion actual del usuario: si se leyera esa, cambiarla
     * reescribiria la historia y la respuesta dejaria de ser confiable.
     */
    @Transactional(readOnly = true)
    public List<VisibilityOutcomeDTO> acceptedConnectionsByVisibility() {
        log.info("BQ 9: midiendo conexiones aceptadas por opcion de visibilidad");

        Map<VisibilityScopeEnum, Long> accepted = new LinkedHashMap<>();
        for (Object[] row : gapRepository.countAcceptedByVisibility(MatchStatusEnum.ACCEPTED)) {
            accepted.put((VisibilityScopeEnum) row[0], (Long) row[1]);
        }

        List<VisibilityOutcomeDTO> result = new ArrayList<>();
        for (Object[] row : gapRepository.countGapsByVisibility()) {
            VisibilityScopeEnum scope = (VisibilityScopeEnum) row[0];
            long published = (Long) row[1];
            long connections = accepted.getOrDefault(scope, 0L);

            result.add(new VisibilityOutcomeDTO(
                    scope, published, connections, rate(connections, published)));
        }

        result.sort((a, b) -> Double.compare(b.getAcceptedPerGap(), a.getAcceptedPerGap()));
        return result;
    }

    private ConnectionMechanismDTO mechanism(String name, long accepted, long rejected, long pending) {
        long answered = accepted + rejected;
        return new ConnectionMechanismDTO(
                name, accepted + rejected + pending, accepted, rejected, pending,
                rate(accepted, answered));
    }

    // Division protegida: sin datos la tasa es 0, no un error
    private double rate(long part, long total) {
        return total == 0 ? 0.0 : Math.round((double) part / total * 10000.0) / 10000.0;
    }
}
