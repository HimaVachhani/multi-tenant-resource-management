package com.edstruments.multitenantresourcemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Tenant (Organization) in the multi-tenant system.
 * Each tenant has its own isolated database schema for complete data segregation.
 */
@Entity
@Table(name = "tenants", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tenant name is required")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Schema name for this tenant - used for schema-per-tenant multi-tenancy.
     * This enables complete data isolation at the database level.
     */
    @NotBlank(message = "Schema name is required")
    @Column(nullable = false, unique = true, name = "schema_name")
    private String schemaName;

    /**
     * Bonus Feature: Dynamic Resource Quota Enforcement
     * Allows customization of quotas per tenant.
     */
    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_resources")
    private Integer maxResources;

    @Column(name = "max_resources_per_user")
    private Integer maxResourcesPerUser;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

