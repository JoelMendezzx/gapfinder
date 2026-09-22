package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.enums.ResponseStatusEnum;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.model.OpenTableParticipantModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.OpenTableParticipantRepository;
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
    public List<OpenTableParticipantModel> getByOpenTable(Long openTableId) {
        return participantRepository.findByOpenTableId(openTableId);
    }

    @Transactional(readOnly = true)
    public List<OpenTableParticipantModel> getByUser(Long userId) {
        return participantRepository.findByUserIdOrderByOpenTableStartTimeDesc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<OpenTableParticipantModel> findParticipant(Long openTableId, Long userId) {
        return participantRepository.findByOpenTableIdAndUserId(openTableId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isUserIn(Long openTableId, Long userId) {
        return participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
                .map(participant -> participant.getRsvp() == ResponseStatusEnum.IN)
                .orElse(false);
    }

    @Transactional
    public OpenTableParticipantModel join(OpenTableModel openTable, UserModel user) {
        Optional<OpenTableParticipantModel> existente = participantRepository.findByOpenTableIdAndUserId(
                openTable.getId(),
                user.getId()
        );

        if (existente.isPresent() && existente.get().getRsvp() == ResponseStatusEnum.IN) {
            throw new IllegalStateException("El usuario ya está unido a esta Open Table");
        }

        OpenTableParticipantModel participant = existente.orElse(new OpenTableParticipantModel());

        participant.setOpenTable(openTable);
        participant.setUser(user);
        participant.setRsvp(ResponseStatusEnum.IN);
        participant.setRespondedAt(LocalDateTime.now());

        return participantRepository.save(participant);
    }

    @Transactional
    public OpenTableParticipantModel leave(Long openTableId, Long userId) {
        OpenTableParticipantModel participant = participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
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

    // Calificar (marcar si disfrutó o no) una Open Table ya finalizada,
    // solo si el usuario fue participante activo de ella
    @Transactional
    public OpenTableParticipantModel rateOpenTable(Long openTableId, Long userId, boolean enjoyed) {
        OpenTableParticipantModel participant = participantRepository.findByOpenTableIdAndUserId(openTableId, userId)
                .orElseThrow(() -> new NotFoundException("El usuario no participó en esta Open Table"));

        if (participant.getRsvp() != ResponseStatusEnum.IN) {
            throw new IllegalStateException("Solo puede calificar quien estuvo dentro de la Open Table");
        }

        if (participant.getOpenTable().getStatus() != OpenTableStatusEnum.ENDED) {
            throw new IllegalStateException("Solo se puede calificar una Open Table que ya finalizó");
        }

        participant.setEnjoyed(enjoyed);

        return participantRepository.save(participant);
    }
}