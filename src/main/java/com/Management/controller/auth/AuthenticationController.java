package com.Management.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        String token = authService.verify(loginDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login Success");
        response.put("token", token);
        if (token != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
