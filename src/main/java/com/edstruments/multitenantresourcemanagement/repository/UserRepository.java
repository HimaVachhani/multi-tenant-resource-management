package com.edstruments.multitenantresourcemanagement.repository;

import com.edstruments.multitenantresourcemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 * 
 * Advanced Multi-Tenancy:
 * All queries automatically operate within the current tenant's schema
 * thanks to Hibernate's schema-per-tenant configuration.
 * 
 * The @Where clause in the User entity ensures soft-deleted users are
 * automatically filtered from all queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndTenantId(String username, Long tenantId);

    boolean existsByUsernameAndTenantId(String username, Long tenantId);

    /**
     * Counts active (non-deleted) users for a tenant.
     * 
     * Performance Optimization:
     * Uses COUNT query instead of loading all users into memory.
     * Index on tenant_id ensures fast execution.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.deleted = false")
    long countByTenantId(@Param("tenantId") Long tenantId);

}

