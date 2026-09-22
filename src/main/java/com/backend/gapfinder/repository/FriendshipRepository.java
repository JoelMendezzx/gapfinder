package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.FriendshipModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipModel, Long> {

    Optional<FriendshipModel> findByRequesterIdAndAddresseeId(Long requesterId, Long addresseeId);

    List<FriendshipModel> findByRequesterIdOrAddresseeId(Long requesterId, Long addresseeId);
}