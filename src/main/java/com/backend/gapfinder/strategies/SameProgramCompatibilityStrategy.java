package com.backend.gapfinder.strategies;

import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class SameProgramCompatibilityStrategy implements CompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_PROGRAM;
    }

    @Override
    public double calculate(UserModel userA, UserModel userB) {
        if (userA.getProgram() == null || userB.getProgram() == null) {
            return 0.0;
        }
        return userA.getProgram().equals(userB.getProgram()) ? 1.0 : 0.0;
    }
}