package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.MessageModel;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageModel, Long> {

    List<MessageModel> findByMatchIdOrderBySentAtAsc(Long matchId);

    List<MessageModel> findByOpenTableIdOrderBySentAtAsc(Long openTableId);

}