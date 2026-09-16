package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchModeEnum;
import org.springframework.stereotype.Component;

@Component
public class SameGapCompatibilityStrategy extends AbstractCompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_GAP;
    }

    @Override
    protected double calculatePrimaryScore(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB) {
        boolean sameStart = gapA.getStartTime().equals(gapB.getStartTime());
        boolean sameEnd = gapA.getEndTime().equals(gapB.getEndTime());
        return (sameStart && sameEnd) ? 1.0 : 0.0;
    }
}
