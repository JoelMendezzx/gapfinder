package com.backend.gapfinder.entities.visibilitysettings;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserService;
import com.backend.gapfinder.enums.VisibilityScopeEnum;
import com.backend.gapfinder.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VisibilitySettingsService {

    private final VisibilitySettingsRepository visibilitySettingsRepository;
    private final UserService userService;

    public VisibilitySettingsService(VisibilitySettingsRepository visibilitySettingsRepository,
                                      UserService userService) {
        this.visibilitySettingsRepository = visibilitySettingsRepository;
        this.userService = userService;
    }

    // Crear la configuración inicial de visibilidad de un usuario (una sola vez por usuario)
    @Transactional
    public VisibilitySettingsEntity create(Long userId, VisibilitySettingsEntity settings) {
        log.info("Inicia proceso de crear configuración de visibilidad para el usuario con id = {}", userId);

        UserEntity user = userService.getById(userId);

        if (visibilitySettingsRepository.existsByUserId(userId)) {
            throw new IllegalStateException("El usuario ya tiene una configuración de visibilidad");
        }

        validateVisibilityScope(settings.getVisibilityScope());

        settings.setId(null);
        settings.setUser(user);

        log.info("Termina proceso de crear configuración de visibilidad para el usuario con id = {}", userId);
        return visibilitySettingsRepository.save(settings);
    }

    // Consultar la configuración de visibilidad de un usuario
    @Transactional(readOnly = true)
    public VisibilitySettingsEntity getByUser(Long userId) {
        log.info("Inicia proceso de consultar configuración de visibilidad del usuario con id = {}", userId);

        userService.getById(userId);

        return visibilitySettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        "El usuario con id " + userId + " no tiene configuración de visibilidad"));
    }

    // Actualizar las preferencias de visibilidad de un usuario
    @Transactional
    public VisibilitySettingsEntity update(Long userId, VisibilitySettingsEntity settings) {
        log.info("Inicia proceso de actualizar configuración de visibilidad del usuario con id = {}", userId);

        VisibilitySettingsEntity existente = getByUser(userId);

        validateVisibilityScope(settings.getVisibilityScope());

        existente.setVisibilityScope(settings.getVisibilityScope());
        existente.setShowSchedule(settings.isShowSchedule());
        existente.setShowInterests(settings.isShowInterests());
        existente.setShowGap(settings.isShowGap());

        log.info("Termina proceso de actualizar configuración de visibilidad del usuario con id = {}", userId);
        return visibilitySettingsRepository.save(existente);
    }

    // Validar que el scope de visibilidad sea obligatorio
    private void validateVisibilityScope(VisibilityScopeEnum scope) {
        if (scope == null) {
            throw new IllegalArgumentException("El alcance de visibilidad es obligatorio");
        }
    }

}