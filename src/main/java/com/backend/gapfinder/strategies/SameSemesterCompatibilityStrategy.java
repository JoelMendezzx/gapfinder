package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchModeEnum;
import org.springframework.stereotype.Component;

@Component
public class SameSemesterCompatibilityStrategy extends AbstractCompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_SEMESTER;
    }

    @Override
    protected double calculatePrimaryScore(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB) {
        if (userA.getSemester() == null || userB.getSemester() == null) {
            return 0.0;
        }
        return userA.getSemester().equals(userB.getSemester()) ? 1.0 : 0.0;
    }
}
