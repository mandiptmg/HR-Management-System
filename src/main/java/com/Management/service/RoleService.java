package com.Management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Management.Model.Role;
import com.Management.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(String roleName) {
        // Check if the role already exists
        Role existingRole = roleRepository.findByName(roleName);
        if (existingRole != null) {
            throw new RuntimeException("Role already exists: " + roleName);
        }

        // Create and save the new role
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role updateRole(Long id, Role roleDetails) {
        // Find the existing role by ID
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role with ID " + id + " not found."));

        // Check if the new role name already exists (to prevent duplicates)
        if (roleRepository.findByName(roleDetails.getName()) != null &&
                !existingRole.getName().equals(roleDetails.getName())) {
            throw new RuntimeException("Role name already exists: " + roleDetails.getName());
        }

        // Update the role name
        existingRole.setName(roleDetails.getName());

        // Save the updated role
        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

}
