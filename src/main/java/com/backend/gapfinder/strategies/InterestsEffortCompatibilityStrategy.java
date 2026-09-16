package com.backend.gapfinder.strategies;

import com.backend.gapfinder.entities.gap.GapEntity;
import com.backend.gapfinder.entities.interest.InterestEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.enums.MatchModeEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InterestsEffortCompatibilityStrategy extends AbstractCompatibilityStrategy {

    @Override
    public MatchModeEnum getMode() {
        return MatchModeEnum.INTERESTS_EFFORT;
    }

    @Override
    protected double calculatePrimaryScore(UserEntity userA, GapEntity gapA, UserEntity userB, GapEntity gapB) {
        Set<Long> interestsA = toInterestIds(userA);
        Set<Long> interestsB = toInterestIds(userB);

        if (interestsA.isEmpty() && interestsB.isEmpty()) {
            return 0.0;
        }

        Set<Long> intersection = new HashSet<>(interestsA);
        intersection.retainAll(interestsB);

        Set<Long> union = new HashSet<>(interestsA);
        union.addAll(interestsB);

        return (double) intersection.size() / union.size();
    }

    private Set<Long> toInterestIds(UserEntity user) {
        Set<Long> ids = new HashSet<>();
        for (InterestEntity interest : user.getInterests()) {
            ids.add(interest.getId());
        }
        return ids;
    }
}
