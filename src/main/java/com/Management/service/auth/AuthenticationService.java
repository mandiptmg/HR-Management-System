package com.Management.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Management.Model.RefreshToken;
import com.Management.Model.Role;
import com.Management.Model.User;
import com.Management.dto.JwtResponseDTO;
import com.Management.dto.LoginDTO;
import com.Management.dto.SignUpDTO;
import com.Management.repository.RoleRepository;
import com.Management.repository.UserRepository;
import com.Management.service.RefreshTokenService;
import com.Management.service.Jwt.JwtService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROLE = "USER";
    private static final long VERIFICATION_EXPIRY_MINUTES = 15;

    // login
    public JwtResponseDTO login(LoginDTO loginDTO) {

        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("Email not found");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        // Validate password manually
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Authenticate user credentials
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        if (authentication == null) {
            throw new RuntimeException("Authentication failed");
        }
        // Generate Tokens
        String accessToken = jwtService.generateAccessToken(loginDTO.getEmail());
        // Generate Refresh Token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDTO.getEmail());

        return JwtResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .message("Login successful")
                .build();

    }

    public SignUpDTO register(SignUpDTO signUpDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new RuntimeException("The provided email is already in use.");
        }

        // Fetch role (default to "USER" if none provided)
        String roleName = signUpDTO.getRole() != null ? signUpDTO.getRole().getName() : DEFAULT_ROLE;
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Role '" + signUpDTO.getRole().getName() + "' not found.");
        }

        // Map DTO to User entity
        User user = new User();
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setEnabled(false);
        user.setRole(role);
        user.setVerificationCode(generateVerificationCode());
        user.setExpiryDate(LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRY_MINUTES));

        // Save and return UserDTO
        User savedUser = userRepository.save(user);

        // Send email
        String verificationLink = generateVerificationLink(user.getVerificationCode());
        sendVerificationEmail(savedUser.getEmail(), verificationLink);

        return modelMapper.map(savedUser, SignUpDTO.class);
    }

    private void sendVerificationEmail(String email, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Email Verification");
            helper.setText("<p>Click the link below to verify your email:</p>"
                    + "<a href=\"" + link + "\">Verify Email</a>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String generateVerificationLink(String verificationCode) {
        return "http://localhost:5173/verify-email?token=" + verificationCode;
    }

    // public void resendVerificationCode(String email) {
    // User user = userRepository.findByEmail(email());

    // if (optionalUser.isPresent()) {
    // User user = optionalUser.get();
    // if (user.isEnabled()) {
    // throw new RuntimeException("Account is already verified");
    // }
    // user.setVerificationCode(generateVerificationCode());
    // user.setExpiryDate(null);(LocalDateTime.now().plusHours(1));
    // sendVerificationEmail(user);
    // userRepository.save(user);
    // } else {
    // throw new RuntimeException("User not found");
    // }
    // }

    // generate verification token
    private String generateVerificationCode() {
        return UUID.randomUUID().toString();
    }
}
