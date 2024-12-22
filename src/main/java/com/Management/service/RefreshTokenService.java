package com.Management.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Management.Model.RefreshToken;
import com.Management.repository.RefreshTokenRepository;
import com.Management.repository.UserRepository;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;


     public RefreshToken createRefreshToken(String email){
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file 
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }



}
