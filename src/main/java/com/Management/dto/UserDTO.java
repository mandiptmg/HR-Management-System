package com.Management.dto;


import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private boolean verified;
    private RoleDTO role;
}
