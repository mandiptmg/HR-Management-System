package com.Management.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("USER"));
        }
    }
}
