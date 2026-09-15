package com.backend.gapfinder.entities.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {

	List<ActivityEntity> findByInterestIdIn(Collection<Long> interestIds);
}
