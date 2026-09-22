package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.FriendshipBasicDTO;
import com.backend.gapfinder.dto.response.UserBasicDTO;
import com.backend.gapfinder.model.FriendshipModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.service.FriendshipService;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final ModelMapper modelMapper;

    public FriendshipController(FriendshipService friendshipService, ModelMapper modelMapper) {
        this.friendshipService = friendshipService;
        this.modelMapper = modelMapper;
    }

    // Envía una solicitud de amistad
    // POST /friendships?requesterId=369&addresseeId=400
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FriendshipBasicDTO sendRequest(
            @RequestParam Long requesterId,
            @RequestParam Long addresseeId) {

        FriendshipModel friendship = friendshipService.sendRequest(requesterId, addresseeId);
        return modelMapper.map(friendship, FriendshipBasicDTO.class);
    }

    // Acepta una solicitud de amistad (requiere validar quién la acepta)
    // PATCH /friendships/57/accept?userId=400
    @PatchMapping("/{id}/accept")
    public FriendshipBasicDTO acceptRequest(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipModel friendship = friendshipService.acceptRequest(id, userId);
        return modelMapper.map(friendship, FriendshipBasicDTO.class);
    }

    // Rechaza una solicitud de amistad
    // PATCH /friendships/57/reject?userId=400
    @PatchMapping("/{id}/reject")
    public FriendshipBasicDTO rejectRequest(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipModel friendship = friendshipService.rejectRequest(id, userId);
        return modelMapper.map(friendship, FriendshipBasicDTO.class);
    }

    // Elimina una amistad
    // DELETE /friendships/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        friendshipService.delete(id);
    }

    // Obtiene los amigos aceptados de un usuario
    // GET /friendships/user/{userId}/friends
    @GetMapping("/user/{userId}/friends")
    public List<UserBasicDTO> getFriendsByUser(@PathVariable Long userId) {
        List<UserModel> friends = friendshipService.getFriendsByUser(userId);
        return modelMapper.map(friends, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }

    // Obtiene los amigos que tienen un GAP activo
    // GET /friendships/user/{userId}/friends/active-gap
    @GetMapping("/user/{userId}/friends/active-gap")
    public List<UserBasicDTO> getFriendsWithActiveGap(@PathVariable Long userId) {
        List<UserModel> friends = friendshipService.getFriendsWithActiveGap(userId);
        return modelMapper.map(friends, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }
}