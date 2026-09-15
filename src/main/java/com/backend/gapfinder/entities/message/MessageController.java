package com.backend.gapfinder.entities.message;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final ModelMapper modelMapper;

    public MessageController(MessageService messageService, ModelMapper modelMapper) {
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    // Envía un mensaje de match o de Open Table
    // POST /messages?senderId=1&matchId=2&content=Hola
    // POST /messages?senderId=1&openTableId=3&content=Hola
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageBasicDTO sendMessage(
            @RequestParam Long senderId,
            @RequestParam(required = false) Long matchId,
            @RequestParam(required = false) Long openTableId,
            @RequestParam String content) {

        MessageEntity message = messageService.send(senderId, matchId, openTableId, content);
        return modelMapper.map(message, MessageBasicDTO.class);
    }

    // Obtiene todos los mensajes de un match
    // GET /messages/match/{matchId}
    @GetMapping("/match/{matchId}")
    public List<MessageBasicDTO> getMessagesByMatch(@PathVariable Long matchId) {
        List<MessageEntity> messages = messageService.getAllByMatch(matchId);
        return modelMapper.map(messages, new TypeToken<List<MessageBasicDTO>>() {}.getType());
    }

    

    // Obtiene todos los mensajes de una Open Table
    // GET /messages/opentable/{openTableId}
    @GetMapping("/opentable/{openTableId}")
    public List<MessageBasicDTO> getMessagesByOpenTable(@PathVariable Long openTableId) {
        List<MessageEntity> messages = messageService.getAllByOpenTable(openTableId);
        return modelMapper.map(messages, new TypeToken<List<MessageBasicDTO>>() {}.getType());
    }

    // Obtiene un mensaje por id
    // GET /messages/{id}
    @GetMapping("/{id}")
    public MessageCompleteDTO getMessage(@PathVariable Long id) {
        MessageEntity message = messageService.getById(id);
        return modelMapper.map(message, MessageCompleteDTO.class);
    }
}
