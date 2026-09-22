package com.backend.gapfinder.service;

import com.backend.gapfinder.enums.MatchStatusEnum;
import com.backend.gapfinder.enums.NotificationTypeEnum;
import com.backend.gapfinder.enums.OpenTableStatusEnum;
import com.backend.gapfinder.events.NotificationEvent;
import com.backend.gapfinder.events.NotificationPublisher;
import com.backend.gapfinder.exceptions.NotFoundException;
import com.backend.gapfinder.model.MatchModel;
import com.backend.gapfinder.model.MessageModel;
import com.backend.gapfinder.model.OpenTableModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MatchService matchService;
    private final OpenTableService openTableService;
    private final OpenTableParticipantService participantService;
    private final NotificationPublisher notificationPublisher;

    public MessageService(MessageRepository messageRepository,
                           UserService userService,
                           MatchService matchService,
                           OpenTableService openTableService,
                           OpenTableParticipantService participantService,
                           NotificationPublisher notificationPublisher) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.matchService = matchService;
        this.openTableService = openTableService;
        this.participantService = participantService;
        this.notificationPublisher = notificationPublisher;
    }

    // Enviar un mensaje: exactamente uno de matchId/openTableId debe venir lleno,
    // y el remitente debe pertenecer a esa conversación (match aceptado o mesa activa con RSVP=in)
    @Transactional
    public MessageModel send(Long senderId, Long matchId, Long openTableId, String content) {
        log.info("Inicia proceso de enviar mensaje del usuario con id = {}", senderId);

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }

        boolean hasMatch = matchId != null;
        boolean hasOpenTable = openTableId != null;

        if (hasMatch == hasOpenTable) {
            throw new IllegalArgumentException("El mensaje debe pertenecer a un match o a una Open Table, no ambos ni ninguno");
        }

        UserModel sender = userService.getById(senderId);

        MessageModel message = new MessageModel();
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        Long notificationUserId = null;

        if (hasMatch) {
            MatchModel match = matchService.getById(matchId);

            boolean isParticipant = match.getRequester().getId().equals(senderId)
                    || match.getReceiver().getId().equals(senderId);
            if (!isParticipant) {
                throw new IllegalArgumentException("El usuario no participa en este match");
            }
            if (match.getStatus() != MatchStatusEnum.HAPPENING_NOW) {
                throw new IllegalStateException("Solo se puede chatear en un match HAPPENING_NOW");
            }

            message.setMatch(match);
                notificationUserId = match.getRequester().getId().equals(senderId)
                    ? match.getReceiver().getId()
                    : match.getRequester().getId();
        } else {
            OpenTableModel openTable = openTableService.getById(openTableId);

            if (openTable.getStatus() != OpenTableStatusEnum.ACTIVE) {
                throw new IllegalStateException("Esta Open Table no está activa");
            }
            if (!participantService.isUserIn(openTableId, senderId)) {
                throw new IllegalArgumentException("El usuario no está dentro de esta Open Table");
            }

            message.setOpenTable(openTable);
        }

        MessageModel saved = messageRepository.save(message);

        if (notificationUserId != null) {
            notificationPublisher.publish(new NotificationEvent(
                notificationUserId,
                NotificationTypeEnum.MESSAGE_RECEIVED,
                saved.getId(),
                sender.getName() + " te envió un mensaje"
            ));
        }

        log.info("Termina proceso de enviar mensaje del usuario con id = {}", senderId);
        return saved;
    }

    // Consultar un mensaje por id
    @Transactional(readOnly = true)
    public MessageModel getById(Long id) {
        log.info("Inicia proceso de consultar el mensaje con id = {}", id);

        return messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El mensaje con id " + id + " no existe"));
    }

    // Consultar los mensajes de un match, en orden cronológico
    @Transactional(readOnly = true)
    public List<MessageModel> getAllByMatch(Long matchId) {
        log.info("Inicia proceso de consultar mensajes del match con id = {}", matchId);

        matchService.getById(matchId);

        return messageRepository.findByMatchIdOrderBySentAtAsc(matchId);
    }

    // Consultar los mensajes de una Open Table, en orden cronológico
    @Transactional(readOnly = true)
    public List<MessageModel> getAllByOpenTable(Long openTableId) {
        log.info("Inicia proceso de consultar mensajes de la Open Table con id = {}", openTableId);

        openTableService.getById(openTableId);

        return messageRepository.findByOpenTableIdOrderBySentAtAsc(openTableId);
    }



}