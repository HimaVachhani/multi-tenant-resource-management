package com.edstruments.multitenantresourcemanagement.repository;

import com.edstruments.multitenantresourcemanagement.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Tenant entity operations.
 * Operates on the public schema (master tenant database).
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySchemaName(String schemaName);

    Optional<Tenant> findByName(String name);

    boolean existsBySchemaName(String schemaName);

    boolean existsByName(String name);

}

