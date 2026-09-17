package com.backend.gapfinder.services.google;

import com.backend.gapfinder.entities.classblock.ClassBlockEntity;
import com.backend.gapfinder.entities.classblock.ClassBlockBasicDTO;
import com.backend.gapfinder.entities.classblock.ClassBlockService;
import com.backend.gapfinder.exceptions.DuplicateClassBlockException;

import com.google.api.services.calendar.model.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleScheduleImportService {

    private final GoogleCalendarService googleCalendarService;
    private final ClassBlockService classBlockService;

    public GoogleScheduleImportService(GoogleCalendarService googleCalendarService,
                                       ClassBlockService classBlockService) {
        this.googleCalendarService = googleCalendarService;
        this.classBlockService = classBlockService;
    }

    public GoogleImportResult importSchedule(Long userId) throws Exception {
        log.info("Inicia importación del horario de Google Calendar para el usuario {}", userId);

        List<Event> events = googleCalendarService.getEvents(userId);

        List<ClassBlockBasicDTO> created = new ArrayList<>();
        int omitidos = 0;

        for (Event event : events) {
            ClassBlockEntity block = GoogleEventMapper.toClassBlock(event);

            if (block == null) {
                continue;
            }

            try {
                ClassBlockEntity saved = classBlockService.create(userId, block);
                created.add(ClassBlockBasicDTO.fromEntity(saved));
            } catch (DuplicateClassBlockException e) {
                omitidos++;
            } catch (IllegalArgumentException e) {
                log.warn("Se omitió un evento inválido: {} ({})", event.getSummary(), e.getMessage());
                omitidos++;
            }
        }

        log.info("Importación terminada para el usuario {}: {} creados, {} omitidos",
                userId, created.size(), omitidos);
        return new GoogleImportResult(created, omitidos);
    }
}
