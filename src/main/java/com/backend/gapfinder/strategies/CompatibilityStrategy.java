package com.backend.gapfinder.strategies;

import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.model.UserModel;
import com.backend.gapfinder.service.MatchService;
// Cada implementación es una forma distinta de calcular el "bono" que se
// suma al score base de intereses. MatchService las usa de forma
// intercambiable a través de este contrato, sin conocer la implementación.
public interface CompatibilityStrategy {
    MatchModeEnum getMode();
    double calculate(UserModel userA, UserModel userB);
}