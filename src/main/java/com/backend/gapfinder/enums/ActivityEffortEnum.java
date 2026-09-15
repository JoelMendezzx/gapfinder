package com.backend.gapfinder.enums;

// Nivel de esfuerzo de una actividad y preferencia de esfuerzo de un usuario.
// El orden de las constantes va de menor a mayor esfuerzo: al recomendar
// actividades para un match se toma el nivel mas restrictivo (el menor) de
// los dos participantes.
public enum ActivityEffortEnum {
    QUIET,
    NORMAL,
    ACTIVE
}
