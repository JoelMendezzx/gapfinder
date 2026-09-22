package com.backend.gapfinder.model;

import com.backend.gapfinder.enums.VisibilityScopeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Entidad que representa la configuración de visibilidad de un usuario
@Entity
@Table(name = "visibility_settings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VisibilitySettingsModel extends BaseModel {

    @Enumerated(EnumType.STRING)
    private VisibilityScopeEnum visibilityScope;

    private boolean showSchedule;

    private boolean showInterests;

    private boolean showGap;

    // Usuario dueño de esta configuración (relación 1 a 1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

}