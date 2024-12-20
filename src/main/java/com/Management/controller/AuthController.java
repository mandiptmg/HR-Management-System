package com.Management.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Management.Model.ApiResponse;
import com.Management.Model.User;
import com.Management.dto.SignUpDTO;
import com.Management.dto.UserDTO;
import com.Management.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> allUsers = userService.getAllUsers();

        if (allUsers.isEmpty()) {
            return buildResponse("error", HttpStatus.NOT_FOUND, "No users found", null);
        }

        return buildResponse("success", HttpStatus.OK, "Users retrieved successfully", allUsers);
    }

    @PostMapping("/add-user")
    public ResponseEntity<ApiResponse<SignUpDTO>> CreateUser(@Valid @RequestBody SignUpDTO signUpDTO) {

        SignUpDTO createUser = userService.createUser(signUpDTO);
        if (createUser != null) {
            return buildResponse("success", HttpStatus.CREATED, "User Created successfully", createUser);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "User failed to create", null);
        }

    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);

        if (userDTO != null) {
            return buildResponse("success", HttpStatus.OK, "User found successfully", userDTO);
        } else {
            return buildResponse("error", HttpStatus.NOT_FOUND, "User not found", null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SignUpDTO>> updateUser(@PathVariable Long id,
            @Valid @RequestBody SignUpDTO signUpDTO) {
        SignUpDTO updateUser = userService.updateUser(id, signUpDTO);
        if (updateUser != null) {
            return buildResponse("success", HttpStatus.OK, "User Updated successfully", updateUser);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "update failed to  user", null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return buildResponse("success", HttpStatus.OK, "User deleted successfully", null);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "Failed to delete user", null);
        }
    }

    // Helper method to create response
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(String status, HttpStatus statusCode, String message,
            T data) {
        ApiResponse<T> response = new ApiResponse<>(status, statusCode.value(), message, data,
                LocalDateTime.now().toString());
        return ResponseEntity.status(statusCode).body(response);
    }
}
