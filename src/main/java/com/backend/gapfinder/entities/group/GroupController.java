package com.backend.gapfinder.entities.group;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper modelMapper;

    public GroupController(GroupService groupService, ModelMapper modelMapper) {
        this.groupService = groupService;
        this.modelMapper = modelMapper;
    }

    // Obtiene todos los grupos
    // GET /groups
    @GetMapping
    public List<GroupBasicDTO> getGroups() {
        List<GroupEntity> groups = groupService.getAll();
        return modelMapper.map(groups, new TypeToken<List<GroupBasicDTO>>() {}.getType());
    }

    // Obtiene los grupos de un usuario
    // GET /groups/user?userId=1
    @GetMapping("/user")
    public List<GroupBasicDTO> getGroupsByUser(@RequestParam Long userId) {
        List<GroupEntity> groups = groupService.getByUser(userId);
        return modelMapper.map(groups, new TypeToken<List<GroupBasicDTO>>() {}.getType());
    }

    // Obtiene un grupo dado su id
    // GET /groups/{id}

    @GetMapping("/{id:[0-9]+}")
    public GroupCompleteDTO getGroup(@PathVariable Long id) {
        GroupEntity group = groupService.getById(id);
        return modelMapper.map(group, GroupCompleteDTO.class);
    }

    // Crea un nuevo grupo
    // POST /groups?creatorId=1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupBasicDTO createGroup(@RequestParam Long creatorId, @RequestBody GroupBasicDTO groupDTO) {
        GroupEntity groupEntity = modelMapper.map(groupDTO, GroupEntity.class);
        GroupEntity created = groupService.create(creatorId, groupEntity);
        return modelMapper.map(created, GroupBasicDTO.class);
    }

    // Actualiza un grupo existente
    // PUT /groups/{id}
    @PutMapping("/{id}")
    public GroupBasicDTO updateGroup(@PathVariable Long id, @RequestBody GroupBasicDTO groupDTO) {
        GroupEntity groupEntity = modelMapper.map(groupDTO, GroupEntity.class);
        GroupEntity updated = groupService.update(id, groupEntity);
        return modelMapper.map(updated, GroupBasicDTO.class);
    }

    // Elimina un grupo
    // DELETE /groups/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
    }

    // Invita (agrega) a un amigo como miembro del grupo
    // POST /groups/{id}/members?requesterId=1&newMemberId=2
    @PostMapping("/{id}/members")
    public GroupBasicDTO addMember(
            @PathVariable Long id,
            @RequestParam Long requesterId,
            @RequestParam Long newMemberId) {

        GroupEntity updated = groupService.addMember(id, requesterId, newMemberId);
        return modelMapper.map(updated, GroupBasicDTO.class);
    }
}
