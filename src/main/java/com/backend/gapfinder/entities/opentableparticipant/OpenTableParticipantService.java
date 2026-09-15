package com.backend.gapfinder.entities.opentableparticipant;

import com.backend.gapfinder.entities.opentable.OpenTableEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OpenTableParticipantService {

    private final OpenTableParticipantRepository participantRepository;

    public OpenTableParticipantService(OpenTableParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Transactional(readOnly = true)
    public List<OpenTableParticipantEntity> getByOpenTable(Long openTableId) {
        return participantRepository.findByOpenTableId(openTableId);
    }

    @Transactional(readOnly = true)
    public Optional<OpenTableParticipantEntity> findParticipant(Long openTableId, Long userId) {
        return participantRepository.findByOpenTableIdAndUserId(openTableId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isUserIn(Long openTableId, Long userId) {
        return participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
                .map(participant -> participant.getRsvp() == ResponseStatusEnum.IN)
                .orElse(false);
    }

    @Transactional
    public OpenTableParticipantEntity join(OpenTableEntity openTable, UserEntity user) {
        Optional<OpenTableParticipantEntity> existente = participantRepository.findByOpenTableIdAndUserId(
                openTable.getId(),
                user.getId()
        );

        if (existente.isPresent() && existente.get().getRsvp() == ResponseStatusEnum.IN) {
            throw new IllegalStateException("El usuario ya está unido a esta Open Table");
        }

        OpenTableParticipantEntity participant = existente.orElse(new OpenTableParticipantEntity());

        participant.setOpenTable(openTable);
        participant.setUser(user);
        participant.setRsvp(ResponseStatusEnum.IN);
        participant.setRespondedAt(LocalDateTime.now());

        return participantRepository.save(participant);
    }

    @Transactional
    public OpenTableParticipantEntity invite(OpenTableEntity openTable, UserEntity user) {
        OpenTableParticipantEntity participant = participantRepository.findByOpenTableIdAndUserId(
                openTable.getId(),
                user.getId()
        ).orElse(new OpenTableParticipantEntity());

        participant.setOpenTable(openTable);
        participant.setUser(user);
        participant.setRsvp(ResponseStatusEnum.PENDING);
        participant.setRespondedAt(null);

        return participantRepository.save(participant);
    }

    @Transactional
    public OpenTableParticipantEntity respondToInvite(Long openTableId, Long userId, boolean accept) {
        OpenTableParticipantEntity participant = participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
                .orElseThrow(() -> new NotFoundException("El usuario no tiene una invitación a esta Open Table"));

        if (participant.getRsvp() != ResponseStatusEnum.PENDING) {
            throw new IllegalStateException("Esta invitación ya fue respondida");
        }

        participant.setRsvp(accept ? ResponseStatusEnum.IN : ResponseStatusEnum.OUT);
        participant.setRespondedAt(LocalDateTime.now());

        return participantRepository.save(participant);
    }

    @Transactional
    public OpenTableParticipantEntity leave(Long openTableId, Long userId) {
        OpenTableParticipantEntity participant = participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
                .orElseThrow(() -> new NotFoundException("El usuario no pertenece a esta Open Table"));

        if (participant.getRsvp() != ResponseStatusEnum.IN) {
            throw new IllegalStateException("El usuario no está actualmente dentro de la Open Table");
        }

        participant.setRsvp(ResponseStatusEnum.OUT);
        participant.setRespondedAt(LocalDateTime.now());

        return participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public long countActiveParticipants(Long openTableId) {
        return participantRepository.findByOpenTableId(openTableId).stream()
                .filter(participant -> participant.getRsvp() == ResponseStatusEnum.IN)
                .count();
    }
}