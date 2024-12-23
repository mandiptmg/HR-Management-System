package com.Management.controller.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Management.Model.ApiResponse;
import com.Management.Model.RefreshToken;
import com.Management.Model.User;
import com.Management.dto.JwtResponseDTO;
import com.Management.dto.LoginDTO;
import com.Management.dto.RefreshTokenRequestDTO;
import com.Management.dto.SignUpDTO;
import com.Management.repository.UserRepository;
import com.Management.service.RefreshTokenService;
import com.Management.service.Jwt.JwtService;
import com.Management.service.auth.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        JwtResponseDTO token = authService.login(loginDTO);
        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDTO> generateRefreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenDTO) {
        try {
            RefreshToken validToken = refreshTokenService.validateRefreshToken(refreshTokenDTO.getRefreshToken());

            User user = validToken.getUser();

            String accessToken = jwtService.generateAccessToken(user.getEmail());
            String newRefreshToken = validToken.getRefreshToken();

            // Return the response
            return ResponseEntity.ok(
                    JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(newRefreshToken)
                            .message("Token refreshed successfully")
                            .build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JwtResponseDTO.builder()
                            .accessToken(null)
                            .refreshToken(null)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JwtResponseDTO.builder()
                            .accessToken(null)
                            .refreshToken(null)
                            .message("Failed to refresh token")
                            .build());
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        return ResponseEntity.ok("Logged out successfully, refresh token revoked.");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return buildResponse("error", HttpStatus.UNAUTHORIZED, "User not authenticated", null);
        }

        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user != null) {
            return buildResponse("success", HttpStatus.OK, "User found", user);
        } else {
            return buildResponse("error", HttpStatus.NOT_FOUND, "User not found", null);
        }

    }

    // register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SignUpDTO>> CreateUser(@Valid @RequestBody SignUpDTO signUpDTO) {

        SignUpDTO createUser = authService.register(signUpDTO);
        if (createUser != null) {
            return buildResponse("success", HttpStatus.CREATED,
                    "User registered successfully. Check your email for verification.", createUser);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "User registered fail. please try agin ", null);
        }

    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<User>> verifyEmail(@RequestParam("token") String token) {
        // Find user by verification code
        Optional<User> optionalUser = userRepository.findByVerificationCode(token);

        // Check if the token is invalid
        if (optionalUser.isEmpty()) {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "Invalid token.", null);
        }

        User user = optionalUser.get();

        // Check if the token has expired
        if (user.getExpiryDate().isBefore(LocalDateTime.now())) {
            // If the user's account is disabled, delete the user
            if (!user.isEnabled()) {
                userRepository.delete(user);
                return buildResponse("error", HttpStatus.BAD_REQUEST,
                        "Token has expired. Disabled user has been deleted.", null);
            } else {
                return buildResponse("error", HttpStatus.BAD_REQUEST, "Token has expired, but the user is active.",
                        null);
            }
        }

        // Activate the user account
        user.setEnabled(true);
        user.setVerificationCode(null); // Clear the verification code
        user.setExpiryDate(null); // Clear expiry date
        userRepository.save(user);

        // Return success response
        return buildResponse("success", HttpStatus.OK, "Email verified successfully. You can now log in.", null);
    }

    // Helper method to create response
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(String status, HttpStatus statusCode, String message,
            T data) {
        ApiResponse<T> response = new ApiResponse<>(status, statusCode.value(), message, data,
                LocalDateTime.now().toString());
        return ResponseEntity.status(statusCode).body(response);
    }
}
