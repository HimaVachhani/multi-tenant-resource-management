package com.edstruments.multitenantresourcemanagement.controller;

import com.edstruments.multitenantresourcemanagement.service.AuditLogService;
import com.edstruments.multitenantresourcemanagement.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Admin only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getLogs(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "20") int size,
                                     HttpServletRequest request) {
        Claims claims = com.edstruments.multitenantresourcemanagement.security.SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        Page<?> logs = auditLogService.getForTenant(tokenTenant, page, size);
        return ResponseEntity.ok(logs);
    }
}
