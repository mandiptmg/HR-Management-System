package com.Management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Management.Model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
}
