package com.backend.gapfinder.repository;

import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.model.ActivityModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ActivityRepository extends JpaRepository<ActivityModel, Long> {

	List<ActivityModel> findByInterestIdIn(Collection<Long> interestIds);

	List<ActivityModel> findByDurationMinutesAndActivityEffortLevelAndInterestId(
			Integer durationMinutes, ActivityEffortEnum effort, Long interestId);

	List<ActivityModel> findByDurationMinutesLessThanEqual(Integer durationMinutes);

	List<ActivityModel> findByDurationMinutesLessThanEqualAndInterestIdIn(
			Integer durationMinutes, Set<Long> interestIds);

	List<ActivityModel> findByDurationMinutesLessThanEqualAndActivityEffortLevel(
			Integer durationMinutes, ActivityEffortEnum effort);

	List<ActivityModel> findByDurationMinutesLessThanEqualAndActivityEffortLevelAndInterestIdIn(
			Integer durationMinutes, ActivityEffortEnum effort, Set<Long> interestIds);
}
