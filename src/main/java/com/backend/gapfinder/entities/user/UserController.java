package com.backend.gapfinder.entities.user;

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
        List<UserEntity> users = userService.getAll();
        return modelMapper.map(users, new TypeToken<List<UserBasicDTO>>() {}.getType());
    }

    // Obtiene un usuario dado su id
    // GET /users/{id}
    @GetMapping("/{id}")
    public UserCompleteDTO getUser(@PathVariable Long id) {
        UserEntity user = userService.getById(id);
        return modelMapper.map(user, UserCompleteDTO.class);
    }

    // Busca un usuario por email
    // GET /users/by-email?email=juan@uniandes.edu.co
    @GetMapping("/by-email")
    public UserBasicDTO getByEmail(@RequestParam String email) {
        UserEntity user = userService.findByEmail(email);
        return modelMapper.map(user, UserBasicDTO.class);
    }

    // Crea un nuevo usuario
    // POST /users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserBasicDTO createUser(@RequestBody UserBasicDTO userDTO) {
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        UserEntity created = userService.create(userEntity);
        return modelMapper.map(created, UserBasicDTO.class);
    }

    // Actualiza el perfil de un usuario existente
    // PUT /users/{id}
    @PutMapping("/{id}")
    public UserBasicDTO updateUser(@PathVariable Long id, @RequestBody UserBasicDTO userDTO) {
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        UserEntity updated = userService.update(id, userEntity);
        return modelMapper.map(updated, UserBasicDTO.class);
    }

    // Actualiza la ubicación actual (building) de un usuario
    // PATCH /users/{id}/location?buildingId=3
    @PatchMapping("/{id}/location")
    public UserBasicDTO updateCurrentLocation(@PathVariable Long id, @RequestParam Long buildingId) {
        UserEntity updated = userService.updateCurrentLocation(id, buildingId);
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
        UserEntity updated = userService.addInterest(userId, interestId);
        return modelMapper.map(updated, UserCompleteDTO.class);
    }

    // DELETE /users/{userId}/interests/{interestId}
    @DeleteMapping("/{userId}/interests/{interestId}")
    public UserCompleteDTO removeInterest(@PathVariable Long userId, @PathVariable Long interestId) {
        UserEntity updated = userService.removeInterest(userId, interestId);
        return modelMapper.map(updated, UserCompleteDTO.class);
    }

}