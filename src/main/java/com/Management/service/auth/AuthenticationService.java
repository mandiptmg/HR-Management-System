package com.Management.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Management.Model.User;
import com.Management.dto.LoginDTO;
import com.Management.repository.UserRepository;
import com.Management.service.Jwt.JwtService;

@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String verify(LoginDTO loginDTO) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail()); // Use email instead of
                                                                                              // username

        // Validate password securely
        if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid email or password"); // Consider a custom exception for better handling
        }
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("Email not found");
        }

        // if (!user.isEnabled()) {
        // throw new RuntimeException("Account not verified. Please verify your
        // account.");
        // }

        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginDTO.getEmail());
        } else {
            throw new RuntimeException("Failed to login.");

        }
    }

}
