package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.InterestBasicDTO;
import com.backend.gapfinder.dto.response.UserBasicDTO;
import com.backend.gapfinder.dto.response.UserCompleteDTO;
import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.model.InterestModel;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.service.UserService;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    // Obtiene todos los usuarios
    // GET /users
    @GetMapping
    public List<UserBasicDTO> getUsers() {
        List<UserModel> users = userService.getAll();
        return modelMapper.map(users, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }

    // Obtiene un usuario dado su id
    // GET /users/{id}
    @GetMapping("/{id}")
    public UserCompleteDTO getUser(@PathVariable Long id) {
        UserModel user = userService.getById(id);
        return modelMapper.map(user, UserCompleteDTO.class);
    }

    // Busca un usuario por email
    // GET /users/by-email?email=juan@uniandes.edu.co
    @GetMapping("/by-email")
    public UserBasicDTO getByEmail(@RequestParam String email) {
        UserModel user = userService.findByEmail(email);
        return modelMapper.map(user, UserBasicDTO.class);
    }

    // Busca usuarios por nombre
    // GET /users/by-name?name=Juan
    @GetMapping("/by-name")
    public List<UserBasicDTO> getByName(@RequestParam String name) {
        List<UserModel> users = userService.findByName(name);
        return modelMapper.map(users, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }

    // Crea un nuevo usuario
    // POST /users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserBasicDTO createUser(@RequestBody UserBasicDTO userDTO) {
        UserModel userModel = modelMapper.map(userDTO, UserModel.class);
        UserModel created = userService.create(userModel);
        return modelMapper.map(created, UserBasicDTO.class);
    }

    // Actualiza el perfil de un usuario existente
    // PUT /users/{id}
    @PutMapping("/{id}")
    public UserBasicDTO updateUser(@PathVariable Long id, @RequestBody UserBasicDTO userDTO) {
        UserModel userModel = modelMapper.map(userDTO, UserModel.class);
        UserModel updated = userService.update(id, userModel);
        return modelMapper.map(updated, UserBasicDTO.class);
    }

    // Actualiza la ubicación actual (building) de un usuario a partir de coordenadas GPS
    // PATCH /users/{id}/location?latitude=4.60&longitude=-74.06
    @PatchMapping("/{id}/location")
    public UserBasicDTO updateCurrentLocation(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        UserModel updated = userService.updateCurrentLocation(id, latitude, longitude);
        return modelMapper.map(updated, UserBasicDTO.class);
    }
    // Eliminar usuario: fuera de alcance del proyecto (decisión ya tomada).
    // Descomentar solo si se decide reactivar esta función, junto con
    // el método delete correspondiente en UserService.
    /*
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
    */

    // POST /users/{userId}/interests/{interestId}
    @PostMapping("/{userId}/interests/{interestId}")
    public UserCompleteDTO addInterest(@PathVariable Long userId, @PathVariable Long interestId) {
        UserModel updated = userService.addInterest(userId, interestId);
        return modelMapper.map(updated, UserCompleteDTO.class);
    }

    // DELETE /users/{userId}/interests/{interestId}
    @DeleteMapping("/{userId}/interests/{interestId}")
    public UserCompleteDTO removeInterest(@PathVariable Long userId, @PathVariable Long interestId) {
        UserModel updated = userService.removeInterest(userId, interestId);
        return modelMapper.map(updated, UserCompleteDTO.class);
    }

    // GET /users/{userId}/interests
    @GetMapping("/{userId}/interests")
    public List<InterestBasicDTO> getUserInterests(@PathVariable Long userId) {
        List<InterestModel> interests = userService.getInterests(userId);
        return modelMapper.map(interests, new TypeToken<List<InterestBasicDTO>>() {}.getType());
    }

    // Actualiza la preferencia de esfuerzo de un usuario
    // PATCH /users/{id}/effort?effort=QUIET
    @PatchMapping("/{id}/effort")
    public UserBasicDTO updateEffortPreference(@PathVariable Long id, @RequestParam ActivityEffortEnum effort) {
        UserModel updated = userService.updateEffortPreference(id, effort);
        return modelMapper.map(updated, UserBasicDTO.class);
    }

}