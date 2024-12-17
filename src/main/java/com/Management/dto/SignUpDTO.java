package com.Management.dto;

import lombok.Data;

@Data
public class SignUpDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private RoleDTO role;
}
