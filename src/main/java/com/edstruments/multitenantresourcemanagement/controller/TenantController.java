package com.edstruments.multitenantresourcemanagement.controller;

import com.edstruments.multitenantresourcemanagement.entity.Tenant;
import com.edstruments.multitenantresourcemanagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    // Super-admin only: create tenant
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody Tenant tenant) {
        Tenant created = tenantService.createTenant(tenant);
        return ResponseEntity.ok(created);
    }

    // Super-admin only: delete tenant
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
