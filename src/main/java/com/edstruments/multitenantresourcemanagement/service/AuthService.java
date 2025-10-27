package com.edstruments.multitenantresourcemanagement.service;

import com.edstruments.multitenantresourcemanagement.entity.User;
import com.edstruments.multitenantresourcemanagement.repository.UserRepository;
import com.edstruments.multitenantresourcemanagement.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String username, String password, Long tenantId) {
        User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid username or tenant"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtTokenProvider.generateToken(username, tenantId, user.getRole().name());
    }
}
