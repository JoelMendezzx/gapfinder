package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.RefreshTokenModel;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {
    Optional<RefreshTokenModel> findByToken(String token);
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
