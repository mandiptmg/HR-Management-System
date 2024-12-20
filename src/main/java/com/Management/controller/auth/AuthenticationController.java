package com.Management.controller.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Management.Model.ApiResponse;
import com.Management.Model.User;
import com.Management.dto.LoginDTO;
import com.Management.dto.SignUpDTO;
import com.Management.repository.UserRepository;
import com.Management.service.auth.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login Success");
        response.put("token", token);
        if (token != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
