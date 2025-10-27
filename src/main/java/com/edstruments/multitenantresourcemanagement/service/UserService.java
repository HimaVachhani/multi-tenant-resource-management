package com.edstruments.multitenantresourcemanagement.service;

import com.edstruments.multitenantresourcemanagement.entity.User;
import com.edstruments.multitenantresourcemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${quota.max-users:50}")
    private int defaultMaxUsers;

    @Transactional
    public User createUser(User user) {
        long current = userRepository.countByTenantId(user.getTenantId());
        if (current >= defaultMaxUsers) {
            throw new RuntimeException("Tenant user quota exceeded");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId, Long tenantId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!u.getTenantId().equals(tenantId)) throw new RuntimeException("Unauthorized tenant access");
        // soft delete flag is implemented via @SQLDelete in entity
        userRepository.deleteById(userId);
    }
}
