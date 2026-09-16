package com.backend.gapfinder.entities.activity;

import com.backend.gapfinder.enums.ActivityEffortEnum;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Filtros componibles para sugerir actividades.
 *
 * Existe para que MatchService y OpenTableService no mantengan dos copias de
 * la misma regla. Cuando se agrego el nivel de esfuerzo solo se actualizo una
 * de las dos, y las sugerencias de Open Table siguieron ofreciendo actividades
 * ACTIVE a usuarios QUIET. Cada regla se define aqui una sola vez y los
 * servicios componen las que necesiten.
 */
public final class ActivitySuggestion {

    private ActivitySuggestion() {
    }

    /** La actividad cabe en el tiempo disponible. */
    public static Predicate<ActivityEntity> fitsAvailableTime(int availableMinutes) {
        return activity -> activity.getDurationMinutes() <= availableMinutes;
    }

    /**
     * La actividad toca alguno de los intereses dados. Una actividad sin
     * interes asociado sirve para cualquiera, asi que siempre pasa.
     */
    public static Predicate<ActivityEntity> matchesAnyInterest(Set<Long> interestIds) {
        return activity -> activity.getInterest() == null
                || interestIds.contains(activity.getInterest().getId());
    }

    /**
     * La actividad respeta el nivel de esfuerzo exigido. Si no hay exigencia
     * pasan todas; si la hay, una actividad sin nivel definido no se propone,
     * porque no hay forma de saber si respeta el limite.
     */
    public static Predicate<ActivityEntity> matchesEffort(ActivityEffortEnum required) {
        return activity -> required == null
                || activity.getActivityEffortLevel() == required;
    }

    /**
     * Gana la preferencia mas restrictiva de las dos: si a alguien le sirve
     * algo QUIET, no se proponen actividades mas exigentes. Quien no tiene
     * preferencia no restringe; si ninguno tiene, no se filtra por esfuerzo.
     */
    public static ActivityEffortEnum mostRestrictive(ActivityEffortEnum a, ActivityEffortEnum b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.ordinal() <= b.ordinal() ? a : b;
    }
}
