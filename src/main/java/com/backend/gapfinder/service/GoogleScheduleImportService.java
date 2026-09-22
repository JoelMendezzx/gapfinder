package com.backend.gapfinder.service;

import com.backend.gapfinder.dto.response.ClassBlockBasicDTO;
import com.backend.gapfinder.dto.response.GoogleImportResult;
import com.backend.gapfinder.exceptions.DuplicateClassBlockException;
import com.backend.gapfinder.model.ClassBlockModel;
import com.google.api.services.calendar.model.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleScheduleImportService {

    private final GoogleCalendarService googleCalendarService;
    private final ClassBlockService classBlockService;
    private final GapService gapService; // 👈 nueva dependencia

    public GoogleScheduleImportService(GoogleCalendarService googleCalendarService,
                                       ClassBlockService classBlockService,
                                       GapService gapService) {
        this.googleCalendarService = googleCalendarService;
        this.classBlockService = classBlockService;
        this.gapService = gapService;
    }

    public GoogleImportResult importSchedule(Long userId) throws Exception {
        log.info("Inicia importación del horario de Google Calendar para el usuario {}", userId);

        List<Event> events = googleCalendarService.getEvents(userId);
        List<ClassBlockBasicDTO> created = new ArrayList<>();
        int omitidos = 0;

        for (Event event : events) {
            ClassBlockModel block = GoogleEventMapper.toClassBlock(event);
            if (block == null) continue;

            try {
                ClassBlockModel saved = classBlockService.create(userId, block);
                created.add(ClassBlockBasicDTO.fromModel(saved));
            } catch (DuplicateClassBlockException e) {
                omitidos++;
            } catch (IllegalArgumentException e) {
                log.warn("Se omitió un evento inválido: {} ({})", event.getSummary(), e.getMessage());
                omitidos++;
            }
        }

        // 👇 recalcular gaps para hoy (o para cada día distinto que haya en los eventos importados)
        gapService.calculateGapsFromSchedule(userId, LocalDate.now());

        log.info("Importación terminada para el usuario {}: {} creados, {} omitidos",
                userId, created.size(), omitidos);
        return new GoogleImportResult(created, omitidos);
    }
}