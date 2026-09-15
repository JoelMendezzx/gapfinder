package com.backend.gapfinder.entities.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByMatchIdOrderBySentAtAsc(Long matchId);

    List<MessageEntity> findByOpenTableIdOrderBySentAtAsc(Long openTableId);

}