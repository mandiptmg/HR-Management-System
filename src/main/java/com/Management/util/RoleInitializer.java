package com.Management.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Management.Model.Role;
import com.Management.repository.RoleRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class RoleInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        // Initialize roles
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
    }

    // Helper method to check and save role if not present
    private Role createRoleIfNotExists(String roleName) {
        Role ExistRole = roleRepository.findByName(roleName);

        if (ExistRole == null) {
            Role role = new Role(null, roleName); // Let database handle ID generation
            return roleRepository.save(role);
        }
        ;
        return ExistRole;
    }

}
