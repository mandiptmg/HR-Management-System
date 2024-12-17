package com.Management.controller;

import com.Management.Model.ApiResponse;
import com.Management.Model.Role;
import com.Management.dto.RoleDTO;
import com.Management.repository.RoleRepository;
import com.Management.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    RoleRepository roleRepository;

    // Endpoint to get all roles
    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        if (roles.isEmpty()) {
            return buildResponse("error", HttpStatus.NOT_FOUND, "No Roles found", null);
        }

        return buildResponse("success", HttpStatus.OK, "Roles retrieved successfully", roles);
    }

    // Endpoint to create a new role
    @PostMapping("/add-role")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody RoleDTO roleDTO) {
        Role createRole = roleService.createRole(roleDTO.getName());

        if (createRole != null) {
            return buildResponse("success", HttpStatus.CREATED, "Role Created successfully", createRole);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "Role failed to create", null);
        }
    }

    // Endpoint to update an existing role
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @RequestBody Role role) {
        // Call the service to update the role
        Role roleUpdate = roleService.updateRole(id, role);

        if (roleUpdate != null) {
            return buildResponse("success", HttpStatus.OK, "Role Updated successfully", roleUpdate);
        } else {
            return buildResponse("error", HttpStatus.BAD_REQUEST, "Role failed to  user", null);
        }
    }

    // Endpoint to delete a role
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> deleteRole(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            roleService.deleteRole(id);
            ApiResponse<Role> response = new ApiResponse<>(
                    "success",
                    HttpStatus.NO_CONTENT.value(),
                    "Role deleted successfully",
                    null,
                    java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } else {
            ApiResponse<Role> response = new ApiResponse<>(
                    "error",
                    HttpStatus.NOT_FOUND.value(),
                    "Role not found",
                    null,
                    java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);

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
