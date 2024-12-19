package com.Management.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Management.dto.LoginDTO;
import com.Management.service.auth.AuthenticationService;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        return authService.verify(loginDTO);

    }
}
