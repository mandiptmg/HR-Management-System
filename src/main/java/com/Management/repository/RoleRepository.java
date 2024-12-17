package com.Management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Management.Model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
       Role findByName(String name);
}
