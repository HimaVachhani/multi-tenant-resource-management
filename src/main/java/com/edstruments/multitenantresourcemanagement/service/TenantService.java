package com.edstruments.multitenantresourcemanagement.service;

import com.edstruments.multitenantresourcemanagement.entity.Tenant;
import com.edstruments.multitenantresourcemanagement.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DataSource dataSource;

    /**
     * Create tenant record and create schema in DB.
     */
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        // ensure uniqueness checks handled by controller or repository constraints
        Tenant saved = tenantRepository.save(tenant);
        // create schema in DB
        try (Connection conn = dataSource.getConnection()) {
            String schema = tenant.getSchemaName();
            conn.createStatement().execute(String.format("CREATE SCHEMA IF NOT EXISTS %s", schema));
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create tenant schema: " + ex.getMessage(), ex);
        }
        return saved;
    }

    /**
     * Delete tenant record and drop schema (cascade).
     */
    @Transactional
    public void deleteTenant(Long id) {
        Tenant t = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        String schema = t.getSchemaName();
        tenantRepository.deleteById(id);
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schema));
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to drop tenant schema: " + ex.getMessage(), ex);
        }
    }
}
