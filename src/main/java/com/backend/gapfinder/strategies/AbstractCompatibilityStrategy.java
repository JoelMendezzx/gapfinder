package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.user.UserEntity;

public abstract class AbstractCompatibilityStrategy implements CompatibilityStrategy {

    private static final double PRIMARY_WEIGHT = 0.8;
    private static final double EFFORT_WEIGHT = 0.2;

    protected abstract double calculatePrimaryScore(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB);

    @Override
    public final double calculate(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB) {
        double primaryScore = calculatePrimaryScore(userA, gapA, userB, gapB);
        double effortBonus = sameEffortPreference(userA, userB) ? EFFORT_WEIGHT : 0.0;
        double score = (primaryScore * PRIMARY_WEIGHT) + effortBonus;
        return Math.round(score * 100.0) / 100.0;
    }

    private boolean sameEffortPreference(UserEntity userA, UserEntity userB) {
        return userA.getActivityEffortPreference() != null
                && userA.getActivityEffortPreference() == userB.getActivityEffortPreference();
    }
}
