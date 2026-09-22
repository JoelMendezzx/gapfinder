package com.backend.gapfinder.strategies;

import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class SameEffortCompatibilityStrategy implements CompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_EFFORT;
    }

    @Override
    public double calculate(UserModel userA, UserModel userB) {
        if (userA.getActivityEffortPreference() == null || userB.getActivityEffortPreference() == null) {
            return 0.0;
        }
        return userA.getActivityEffortPreference() == userB.getActivityEffortPreference() ? 1.0 : 0.0;
    }
}