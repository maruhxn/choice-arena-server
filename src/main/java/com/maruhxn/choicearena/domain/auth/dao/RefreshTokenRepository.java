package com.maruhxn.choicearena.domain.auth.dao;

import com.maruhxn.choicearena.domain.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByPayload(String payload);

    Optional<RefreshToken> findByEmail(String email);

    void deleteAllByEmail(String email);
}
