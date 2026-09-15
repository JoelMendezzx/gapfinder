package com.backend.gapfinder.entities.classblock;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class-blocks")
public class ClassBlockController {

    private final ClassBlockService classBlockService;
    private final ModelMapper modelMapper;

    public ClassBlockController(ClassBlockService classBlockService, ModelMapper modelMapper) {
        this.classBlockService = classBlockService;
        this.modelMapper = modelMapper;
    }

    // Obtiene una clase dada su id
    // GET /class-blocks/{id}
    @GetMapping("/{id}")
    public ClassBlockBasicDTO getClassBlock(@PathVariable Long id) {
        ClassBlockEntity classBlock = classBlockService.getById(id);
        return modelMapper.map(classBlock, ClassBlockBasicDTO.class);
    }

    // Obtiene el horario completo de un usuario
    // GET /class-blocks/user/{userId}
    @GetMapping("/user/{userId}")
    public List<ClassBlockBasicDTO> getByUser(@PathVariable Long userId) {
        List<ClassBlockEntity> classBlocks = classBlockService.getAllByUser(userId);
        return modelMapper.map(classBlocks, new TypeToken<List<ClassBlockBasicDTO>>() {}.getType());
    }

    // Agrega una clase al horario de un usuario
    // POST /class-blocks/user/{userId}
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ClassBlockBasicDTO createClassBlock(@PathVariable Long userId, @RequestBody ClassBlockBasicDTO dto) {
        ClassBlockEntity classBlockEntity = modelMapper.map(dto, ClassBlockEntity.class);
        ClassBlockEntity created = classBlockService.create(userId, classBlockEntity);
        return modelMapper.map(created, ClassBlockBasicDTO.class);
    }

    // Actualiza una clase existente
    // PUT /class-blocks/{id}
    @PutMapping("/{id}")
    public ClassBlockBasicDTO updateClassBlock(@PathVariable Long id, @RequestBody ClassBlockBasicDTO dto) {
        ClassBlockEntity classBlockEntity = modelMapper.map(dto, ClassBlockEntity.class);
        ClassBlockEntity updated = classBlockService.update(id, classBlockEntity);
        return modelMapper.map(updated, ClassBlockBasicDTO.class);
    }

    // Elimina una clase
    // DELETE /class-blocks/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassBlock(@PathVariable Long id) {
        classBlockService.delete(id);
    }

}