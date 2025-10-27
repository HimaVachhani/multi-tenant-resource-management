package com.edstruments.multitenantresourcemanagement.controller;

import com.edstruments.multitenantresourcemanagement.entity.Resource;
import com.edstruments.multitenantresourcemanagement.enums.AuditAction;
import com.edstruments.multitenantresourcemanagement.security.JwtTokenProvider;
import com.edstruments.multitenantresourcemanagement.security.SecurityUtils;
import com.edstruments.multitenantresourcemanagement.service.AuditLogService;
import com.edstruments.multitenantresourcemanagement.service.ResourceService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuditLogService auditLogService;

    // Admin/Manager create
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<?> createResource(@RequestBody Resource resource, HttpServletRequest request) {
        Claims claims = SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        String username = claims.getSubject();
        // tenant check
        if (!tokenTenant.equals(resource.getTenantId())) {
            return ResponseEntity.status(403).body("Tenant mismatch");
        }
        Resource created = resourceService.createResource(resource);
        auditLogService.log(null, tokenTenant, AuditAction.CREATED_RESOURCE, "Resource", created.getId(), "Created by " + username);
        return ResponseEntity.ok(created);
    }

    // Admin/Manager update
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource payload, HttpServletRequest request) {
        Claims claims = SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        Resource updated = resourceService.updateResource(id, tokenTenant, payload);
        auditLogService.log(null, tokenTenant, AuditAction.UPDATED_RESOURCE, "Resource", id, "Updated resource");
        return ResponseEntity.ok(updated);
    }

    // Admin/Manager delete (soft)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id, HttpServletRequest request) {
        Claims claims = SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        resourceService.deleteResource(id, tokenTenant);
        auditLogService.log(null, tokenTenant, AuditAction.DELETED_RESOURCE, "Resource", id, "Deleted resource");
        return ResponseEntity.noContent().build();
    }

    // Employee and above: list resources
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping
    public ResponseEntity<?> listResources(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "ownerId", required = false) Long ownerId,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                           HttpServletRequest request) {
        Claims claims = SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        Page<Resource> results = resourceService.listResources(tokenTenant, name, ownerId, page, size);
        return ResponseEntity.ok(results);
    }

    // Employee and above: get one resource
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getResource(@PathVariable Long id, HttpServletRequest request) {
        Claims claims = SecurityUtils.getClaimsFromRequest(request, jwtTokenProvider);
        Long tokenTenant = claims.get("tenantId", Number.class).longValue();
        Resource r = resourceService.listResources(tokenTenant, null, null, 0, 1) // quick existence check
                .getContent().stream().filter(res -> res.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return ResponseEntity.ok(r);
    }
}
