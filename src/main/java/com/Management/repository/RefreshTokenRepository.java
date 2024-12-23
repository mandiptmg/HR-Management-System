package com.Management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Management.Model.RefreshToken;
import com.Management.Model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUser(User user);
}
