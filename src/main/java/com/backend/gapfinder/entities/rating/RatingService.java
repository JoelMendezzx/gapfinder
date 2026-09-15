package com.backend.gapfinder.entities.rating;

import com.backend.gapfinder.entities.match.MatchEntity;
import com.backend.gapfinder.entities.match.MatchService;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;
import com.backend.gapfinder.entities.opentable.OpenTableService;
import com.backend.gapfinder.entities.opentableparticipant.OpenTableParticipantService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final MatchService matchService;
    private final OpenTableService openTableService;
    private final OpenTableParticipantService participantService;

    public RatingService(RatingRepository ratingRepository,
                          UserService userService,
                          MatchService matchService,
                          OpenTableService openTableService,
                          OpenTableParticipantService participantService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.matchService = matchService;
        this.openTableService = openTableService;
        this.participantService = participantService;
    }

    // Calificar un Match o una Open Table (exactamente uno de los dos).
    // ratedUserId es opcional: se usa para calificar a una persona específica,
    // null si es una calificación general (por ejemplo, una Open Table grupal)
    @Transactional
    public RatingEntity create(Long raterId, Long matchId, Long openTableId,
                                Long ratedUserId, int rating, Boolean wouldRepeat) {
        log.info("Inicia proceso de crear calificación del usuario con id = {}", raterId);

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }

        boolean hasMatch = matchId != null;
        boolean hasOpenTable = openTableId != null;

        if (hasMatch == hasOpenTable) {
            throw new IllegalArgumentException("La calificación debe ser de un match o de una Open Table, no ambos ni ninguno");
        }

        UserEntity rater = userService.getById(raterId);
        UserEntity ratedUser = ratedUserId != null ? userService.getById(ratedUserId) : null;

        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setRater(rater);
        ratingEntity.setRatedUser(ratedUser);
        ratingEntity.setRating(rating);
        ratingEntity.setWouldRepeat(wouldRepeat);
        ratingEntity.setCreatedAt(LocalDateTime.now());

        if (hasMatch) {
            MatchEntity match = matchService.getById(matchId);

            boolean isParticipant = match.getRequester().getId().equals(raterId)
                    || match.getReceiver().getId().equals(raterId);
            if (!isParticipant) {
                throw new IllegalArgumentException("El usuario no participó en este match");
            }
            if (match.getStatus() != MatchStatusEnum.ACCEPTED) {
                throw new IllegalStateException("Solo se puede calificar un match que fue ACCEPTED");
            }

            if (ratedUser != null) {
                boolean isOtherParticipant = match.getRequester().getId().equals(ratedUserId)
                        || match.getReceiver().getId().equals(ratedUserId);
                if (!isOtherParticipant || ratedUserId.equals(raterId)) {
                    throw new IllegalArgumentException("El usuario calificado debe ser la otra persona del match");
                }
            }

            if (ratingRepository.existsByMatchIdAndRaterId(matchId, raterId)) {
                throw new IllegalStateException("Ya calificaste este match");
            }

            ratingEntity.setMatch(match);
        } else {
            OpenTableEntity openTable = openTableService.getById(openTableId);

            boolean wasParticipant = participantService.findParticipant(openTableId, raterId).isPresent();
            if (!wasParticipant) {
                throw new IllegalArgumentException("El usuario no participó en esta Open Table");
            }

            if (ratedUser != null && !participantService.findParticipant(openTableId, ratedUserId).isPresent()) {
                throw new IllegalArgumentException("El usuario calificado no participó en esta Open Table");
            }

            if (ratingRepository.existsByOpenTableIdAndRaterId(openTableId, raterId)) {
                throw new IllegalStateException("Ya calificaste esta Open Table");
            }

            ratingEntity.setOpenTable(openTable);
        }

        log.info("Termina proceso de crear calificación del usuario con id = {}", raterId);
        return ratingRepository.save(ratingEntity);
    }

    // Consultar una calificación por id
    @Transactional(readOnly = true)
    public RatingEntity getById(Long id) {
        log.info("Inicia proceso de consultar la calificación con id = {}", id);

        return ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La calificación con id " + id + " no existe"));
    }

    // Consultar todas las calificaciones que ha recibido un usuario
    @Transactional(readOnly = true)
    public List<RatingEntity> getAllByRatedUser(Long ratedUserId) {
        log.info("Inicia proceso de consultar calificaciones recibidas por el usuario con id = {}", ratedUserId);

        userService.getById(ratedUserId);

        return ratingRepository.findByRatedUserId(ratedUserId);
    }

}