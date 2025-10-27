package com.edstruments.multitenantresourcemanagement.entity;

import com.edstruments.multitenantresourcemanagement.enums.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs",
       indexes = {
           @Index(name = "idx_audit_user", columnList = "user_id"),
           @Index(name = "idx_audit_tenant", columnList = "tenant_id"),
           @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
           @Index(name = "idx_audit_action", columnList = "action")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 2000)
    private String details;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

}

