package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchModeEnum;

public interface CompatibilityStrategy {
    MatchModeEnum getMode();
    double calculate(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB);
}
