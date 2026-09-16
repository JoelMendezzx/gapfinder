package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchModeEnum;
import org.springframework.stereotype.Component;

@Component
public class SameProgramCompatibilityStrategy extends AbstractCompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_PROGRAM;
    }

    @Override
    protected double calculatePrimaryScore(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB) {
        if (userA.getProgram() == null || userB.getProgram() == null) {
            return 0.0;
        }
        return userA.getProgram().equals(userB.getProgram()) ? 1.0 : 0.0;
    }
}
