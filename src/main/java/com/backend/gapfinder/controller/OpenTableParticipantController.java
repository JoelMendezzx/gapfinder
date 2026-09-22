package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.OpenTableParticipantBasicDTO;
import com.backend.gapfinder.model.OpenTableParticipantModel;
import com.backend.gapfinder.service.OpenTableParticipantService;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open-table-participants/open-table/{openTableId}")
public class OpenTableParticipantController {

    private final OpenTableParticipantService participantService;
    private final ModelMapper modelMapper;

    public OpenTableParticipantController(OpenTableParticipantService participantService, ModelMapper modelMapper) {
        this.participantService = participantService;
        this.modelMapper = modelMapper;
    }

    // Lista todos los participantes (con su RSVP) de una Open Table
    // GET /open-table-participants/open-table/{openTableId}
    @GetMapping
    public List<OpenTableParticipantBasicDTO> getByOpenTable(@PathVariable Long openTableId) {
        List<OpenTableParticipantModel> participants = participantService.getByOpenTable(openTableId);
        return modelMapper.map(participants, new TypeToken<List<OpenTableParticipantBasicDTO>>() {}.getType());
    }

    // Consulta la participación (RSVP) de un usuario específico en una Open Table
    // GET /open-table-participants/open-table/{openTableId}/user/{userId}
    @GetMapping("/user/{userId}")
    public OpenTableParticipantBasicDTO findParticipant(
            @PathVariable Long openTableId,
            @PathVariable Long userId) {

        Optional<OpenTableParticipantModel> participant = participantService.findParticipant(openTableId, userId);

        if (participant.isEmpty()) {
            return null;
        }

        return modelMapper.map(participant.get(), OpenTableParticipantBasicDTO.class);
    }

    // Cuenta cuántos participantes están actualmente "IN" en una Open Table
    // GET /open-table-participants/open-table/{openTableId}/count
    @GetMapping("/count")
    public long countActiveParticipants(@PathVariable Long openTableId) {
        return participantService.countActiveParticipants(openTableId);
    }

}