package com.edstruments.multitenantresourcemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Resource owned by a user within a tenant.
 * Implements soft delete functionality to preserve historical data.
 * 
 * Advanced Feature: Soft Deletes
 * Resources are soft-deleted to maintain audit trail and data integrity.
 * 
 * Performance Optimization:
 * Indexes are strategically placed on frequently queried columns (tenant_id, owner_id, name)
 * to optimize search and filtering operations.
 */
@Entity
@Table(name = "resources",
       indexes = {
           @Index(name = "idx_resource_tenant", columnList = "tenant_id"),
           @Index(name = "idx_resource_owner", columnList = "owner_id"),
           @Index(name = "idx_resource_name", columnList = "name"),
           @Index(name = "idx_resource_deleted", columnList = "deleted"),
           @Index(name = "idx_resource_tenant_deleted", columnList = "tenant_id, deleted")
       })
@SQLDelete(sql = "UPDATE resources SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Resource name is required")
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /**
     * Soft delete implementation.
     * When true, the resource is considered deleted but remains in the database.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

