package com.backend.gapfinder.services.google;

import com.backend.gapfinder.entities.classblock.ClassBlockEntity;
import com.backend.gapfinder.enums.DayOfWeekEnum;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class GoogleEventMapper {

    private static DayOfWeekEnum mapJavaDayOfWeek(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> DayOfWeekEnum.MON;
            case TUESDAY -> DayOfWeekEnum.TUE;
            case WEDNESDAY -> DayOfWeekEnum.WED;
            case THURSDAY -> DayOfWeekEnum.THU;
            case FRIDAY -> DayOfWeekEnum.FRI;
            case SATURDAY -> DayOfWeekEnum.SAT;
            case SUNDAY -> throw new IllegalArgumentException("El horario no admite clases los domingos");
        };
    }

    // Convierte una ocurrencia real de evento en un solo bloque de clase.
    public static ClassBlockEntity toClassBlock(Event event) {
        EventDateTime start = event.getStart();
        EventDateTime end = event.getEnd();

        if (start.getDateTime() == null || end.getDateTime() == null) {
            return null;
        }

        LocalDateTime startDateTime = toLocalDateTime(start);
        LocalDateTime endDateTime = toLocalDateTime(end);

        ClassBlockEntity block = new ClassBlockEntity();
        block.setSubject(event.getSummary());
        block.setLocation(event.getLocation());
        block.setDayOfWeek(mapJavaDayOfWeek(startDateTime.getDayOfWeek()));
        block.setStartTime(startDateTime.toLocalTime());
        block.setEndTime(endDateTime.toLocalTime());
        return block;
    }

    private static LocalDateTime toLocalDateTime(EventDateTime eventDateTime) {
        long millis = eventDateTime.getDateTime() != null
                ? eventDateTime.getDateTime().getValue()
                : eventDateTime.getDate().getValue();
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(millis),
                ZoneId.systemDefault());
    }
}
