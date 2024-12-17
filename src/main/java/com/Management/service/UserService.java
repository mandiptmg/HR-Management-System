package com.Management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Management.Model.Role;
// import com.Management.Model.Role;
import com.Management.Model.User;
import com.Management.dto.SignUpDTO;
import com.Management.dto.UserDTO;
import com.Management.repository.RoleRepository;
import com.Management.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get all users and map them to UserDTO
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    // Get user by ID and map to UserDTO
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? modelMapper.map(user, UserDTO.class) : null;
    }

    public SignUpDTO createUser(SignUpDTO signUpDTO) {
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Check if email already exists
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new RuntimeException("The provided email is already in use.");
        }

        // Map DTO to User entity
        User user = new User();
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(signUpDTO.getPassword()); // Secure this later with password hashing
        user.setEnabled(false);

        // Assign Role
        Role role = roleRepository.findByName(signUpDTO.getRole().getName());
        if (role == null) {
            throw new RuntimeException("Role '" + signUpDTO.getRole().getName() + "' not found.");
        }
        user.setRole(role);

        // Save and return UserDTO
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, SignUpDTO.class);
    }

    public SignUpDTO updateUser(Long userId, SignUpDTO signUpDTO) {
        // Find existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));

        // Check if the email is being updated and already exists
        if (!existingUser.getEmail().equals(signUpDTO.getEmail()) &&
                userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new RuntimeException("The provided email is already in use.");
        }

        // Update user details
        existingUser.setName(signUpDTO.getName());
        existingUser.setEmail(signUpDTO.getEmail());

        // Update password if provided (and hash it)
        if (signUpDTO.getPassword() != null && !signUpDTO.getPassword().isBlank()) {
            existingUser.setPassword(signUpDTO.getPassword());
        }

        // Update role if provided
        if (signUpDTO.getRole() != null && signUpDTO.getRole().getName() != null) {
            Role role = roleRepository.findByName(signUpDTO.getRole().getName());
            if (role == null) {
                throw new RuntimeException("Role '" + signUpDTO.getRole().getName() + "' not found.");
            }
            existingUser.setRole(role);
        }

        // Save updated user to database
        User updatedUser = userRepository.save(existingUser);

        // Map updated User entity back to DTO and return
        return modelMapper.map(updatedUser, SignUpDTO.class);
    }

    // void means no return
    public Boolean deleteUser(Long id) {
        userRepository.deleteById(id);
        return true;
    }

}
