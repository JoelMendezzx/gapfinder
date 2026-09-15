package com.backend.gapfinder.entities.friendship;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    Optional<FriendshipEntity> findByRequesterIdAndAddresseeId(Long requesterId, Long addresseeId);

    List<FriendshipEntity> findByRequesterIdOrAddresseeId(Long requesterId, Long addresseeId);
}