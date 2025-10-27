package com.edstruments.multitenantresourcemanagement.repository;

import com.edstruments.multitenantresourcemanagement.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Resource entity operations.
 * 
 * Advanced Features:
 * 1. Search and Filtering: Supports dynamic search by name and owner
 * 2. Pagination: Efficient handling of large result sets
 * 3. Soft Deletes: Automatically filters deleted resources via @Where clause
 * 4. Performance Optimization: Strategic indexes on frequently queried columns
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /**
     * Finds a resource by ID and tenant ID.
     * Ensures tenant isolation - users can only access resources from their tenant.
     * 
     * @param id Resource ID
     * @param tenantId Tenant ID
     * @return Optional containing resource if found
     */
    Optional<Resource> findByIdAndTenantId(Long id, Long tenantId);

    /**
     * Finds all resources for a tenant with pagination.
     * 
     * Performance Optimization:
     * Pagination prevents loading large datasets into memory.
     * Index on tenant_id ensures fast execution.
     * 
     * @param tenantId Tenant ID
     * @param pageable Pagination parameters
     * @return Page of resources
     */
    Page<Resource> findByTenantId(Long tenantId, Pageable pageable);

    /**
     * Advanced Feature: Search and Filtering
     * Supports searching resources by name with wildcard matching.
     * 
     * Performance Optimization:
     * Index on name column enables efficient LIKE queries.
     * 
     * @param tenantId Tenant ID
     * @param name Name search pattern
     * @param pageable Pagination parameters
     * @return Page of matching resources
     */
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId " +
           "AND r.deleted = false " +
           "AND LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Resource> searchByName(@Param("tenantId") Long tenantId,
                                @Param("name") String name,
                                Pageable pageable);

    /**
     * Advanced Feature: Search and Filtering
     * Filters resources by owner with pagination.
     * 
     * @param tenantId Tenant ID
     * @param ownerId Owner user ID
     * @param pageable Pagination parameters
     * @return Page of resources owned by the user
     */
    Page<Resource> findByTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    /**
     * Advanced Feature: Search and Filtering
     * Complex search supporting both name and owner filters.
     * 
     * @param tenantId Tenant ID
     * @param name Name search pattern (optional)
     * @param ownerId Owner user ID (optional)
     * @param pageable Pagination parameters
     * @return Page of matching resources
     */
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId " +
           "AND r.deleted = false " +
           "AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:ownerId IS NULL OR r.ownerId = :ownerId)")
    Page<Resource> searchResources(@Param("tenantId") Long tenantId,
                                   @Param("name") String name,
                                   @Param("ownerId") Long ownerId,
                                   Pageable pageable);

    /**
     * Counts resources for a specific user.
     * Used for quota enforcement (max 10 resources per user).
     * 
     * Performance Optimization:
     * COUNT query avoids loading all resources into memory.
     * Composite index on (tenant_id, owner_id) ensures fast execution.
     * 
     * @param ownerId Owner user ID
     * @param tenantId Tenant ID
     * @return Count of resources owned by the user
     */
    @Query("SELECT COUNT(r) FROM Resource r WHERE r.ownerId = :ownerId " +
           "AND r.tenantId = :tenantId AND r.deleted = false")
    long countByOwnerIdAndTenantId(@Param("ownerId") Long ownerId,
                                   @Param("tenantId") Long tenantId);

    /**
     * Counts total resources for a tenant.
     * Used for quota enforcement (max 500 resources per tenant).
     * 
     * @param tenantId Tenant ID
     * @return Count of resources in the tenant
     */
    @Query("SELECT COUNT(r) FROM Resource r WHERE r.tenantId = :tenantId AND r.deleted = false")
    long countByTenantId(@Param("tenantId") Long tenantId);


}

