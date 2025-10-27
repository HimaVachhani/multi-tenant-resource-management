package com.edstruments.multitenantresourcemanagement.controller;

import com.edstruments.multitenantresourcemanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String password = (String) payload.get("password");
        Long tenantId = Long.valueOf(payload.get("tenantId").toString());

        String token = authService.login(username, password, tenantId);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
