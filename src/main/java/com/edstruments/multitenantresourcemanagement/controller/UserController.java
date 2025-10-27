package com.edstruments.multitenantresourcemanagement.controller;

import com.edstruments.multitenantresourcemanagement.entity.User;
import com.edstruments.multitenantresourcemanagement.security.JwtTokenProvider;
import com.edstruments.multitenantresourcemanagement.security.SecurityUtils;
import com.edstruments.multitenantresourcemanagement.service.AuditLogService;
import com.edstruments.multitenantresourcemanagement.service.UserService;
import com.edstruments.multitenantresourcemanagement.enums.AuditAction;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    // Admin only: create user within tenant
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        // Tenant validation: user's tenantId must match token tenant
        Claims claims = jakartaClaims(request);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        if (!tokenTenant.equals(user.getTenantId())) {
            return ResponseEntity.status(403).body("Tenant mismatch");
        }
        User created = userService.createUser(user);
        // log
        Long userId = claims.getSubject() != null ? null : null;
        auditLogService.log(null, tokenTenant, AuditAction.CREATED_USER, "User", created.getId(), "Created user " + created.getUsername());
        return ResponseEntity.ok(created);
    }

    // Admin only: delete user within tenant
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Claims claims = jakartaClaims(request);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        userService.deleteUser(id, tokenTenant);
        auditLogService.log(null, tokenTenant, AuditAction.DELETED_USER, "User", id, "Deleted user " + id);
        return ResponseEntity.noContent().build();
    }

    private Claims jakartaClaims(HttpServletRequest request) {
        return SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
    }
}
