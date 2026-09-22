package com.backend.gapfinder.strategies;

import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class SameSemesterCompatibilityStrategy implements CompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.SAME_SEMESTER;
    }

    @Override
    public double calculate(UserModel userA, UserModel userB) {
        if (userA.getSemester() == null || userB.getSemester() == null) {
            return 0.0;
        }
        return userA.getSemester().equals(userB.getSemester()) ? 1.0 : 0.0;
    }
}