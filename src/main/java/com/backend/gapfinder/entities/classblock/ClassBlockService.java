package com.backend.gapfinder.entities.classblock;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ClassBlockService {

    private final ClassBlockRepository classBlockRepository;
    private final UserService userService;

    public ClassBlockService(ClassBlockRepository classBlockRepository, UserService userService) {
        this.classBlockRepository = classBlockRepository;
        this.userService = userService;
    }

    // Consultar una clase por id
    @Transactional
    public ClassBlockEntity getById(Long id) {
        log.info("Inicia proceso de consultar la clase con id = {}", id);
        return classBlockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La clase con id " + id + " no existe"));
    }

    // Consultar el horario completo de un usuario
    @Transactional
    public List<ClassBlockEntity> getAllByUser(Long userId) {
        log.info("Inicia proceso de consultar el horario del usuario con id = {}", userId);

        // Se valida que el usuario exista antes de consultar sus clases
        userService.getById(userId);

        return classBlockRepository.findByUserId(userId);
    }

    // Agregar una clase al horario de un usuario
    @Transactional
    public ClassBlockEntity create(Long userId, ClassBlockEntity classBlock) {
        log.info("Inicia proceso de creación de una clase para el usuario con id = {}", userId);

        validateClassBlockData(classBlock);

        UserEntity user = userService.getById(userId);
        classBlock.setId(null);
        classBlock.setUser(user);

        log.info("Termina proceso de creación de una clase para el usuario con id = {}", userId);
        return classBlockRepository.save(classBlock);
    }

    // Editar los datos de una clase existente
    @Transactional
    public ClassBlockEntity update(Long id, ClassBlockEntity classBlock) {
        log.info("Inicia proceso de actualización de la clase con id = {}", id);

        ClassBlockEntity existente = getById(id);
        validateClassBlockData(classBlock);

        existente.setSubject(classBlock.getSubject());
        existente.setLocation(classBlock.getLocation());
        existente.setDayOfWeek(classBlock.getDayOfWeek());
        existente.setStartTime(classBlock.getStartTime());
        existente.setEndTime(classBlock.getEndTime());

        log.info("Termina proceso de actualización de la clase con id = {}", id);
        return classBlockRepository.save(existente);
    }

    // Eliminar una clase existente
    @Transactional
    public void delete(Long id) {
        log.info("Inicia proceso de eliminación de la clase con id = {}", id);

        ClassBlockEntity existente = getById(id);
        classBlockRepository.delete(existente);

        log.info("Termina proceso de eliminación de la clase con id = {}", id);
    }

    // Validar que los datos de la clase sean correctos
    private void validateClassBlockData(ClassBlockEntity classBlock) {
        if (classBlock.getSubject() == null || classBlock.getSubject().isBlank()) {
            throw new IllegalArgumentException("La materia es obligatoria");
        }
        if (classBlock.getDayOfWeek() == null) {
            throw new IllegalArgumentException("El día de la semana es obligatorio");
        }
        if (classBlock.getStartTime() == null || classBlock.getEndTime() == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias");
        }
        if (!classBlock.getStartTime().isBefore(classBlock.getEndTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser antes de la hora de fin");
        }
    }
}