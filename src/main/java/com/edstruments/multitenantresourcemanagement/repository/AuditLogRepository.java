package com.edstruments.multitenantresourcemanagement.repository;

import com.edstruments.multitenantresourcemanagement.entity.AuditLog;
import com.edstruments.multitenantresourcemanagement.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for AuditLog entity operations.
 * 
 * Performance Optimization:
 * Strategic indexes on userId, tenantId, timestamp, and action enable
 * efficient querying of audit logs even with large datasets.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Retrieves audit logs for a tenant with pagination.
     * Ordered by timestamp descending (newest first).
     * 
     * @param tenantId Tenant ID
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    Page<AuditLog> findByTenantIdOrderByTimestampDesc(Long tenantId, Pageable pageable);

    /**
     * Retrieves audit logs for a specific user.
     * 
     * @param userId User ID
     * @param tenantId Tenant ID
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    Page<AuditLog> findByUserIdAndTenantIdOrderByTimestampDesc(Long userId, Long tenantId, 
                                                                Pageable pageable);

    /**
     * Retrieves audit logs filtered by action type.
     * 
     * @param tenantId Tenant ID
     * @param action Audit action type
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    Page<AuditLog> findByTenantIdAndActionOrderByTimestampDesc(Long tenantId, AuditAction action, 
                                                                Pageable pageable);

    /**
     * Advanced filtering: Retrieves audit logs within a time range.
     * 
     * @param tenantId Tenant ID
     * @param startDate Start of time range
     * @param endDate End of time range
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    Page<AuditLog> findByTenantIdAndTimestampBetween(@Param("tenantId") Long tenantId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);

}

