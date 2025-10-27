package com.edstruments.multitenantresourcemanagement.service;

import com.edstruments.multitenantresourcemanagement.entity.AuditLog;
import com.edstruments.multitenantresourcemanagement.enums.AuditAction;
import com.edstruments.multitenantresourcemanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(Long userId, Long tenantId, AuditAction action, String entityType, Long entityId, String details) {
        AuditLog a = AuditLog.builder()
                .userId(userId)
                .tenantId(tenantId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(a);
    }

    public Page<AuditLog> getForTenant(Long tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return auditLogRepository.findByTenantIdOrderByTimestampDesc(tenantId, pageable);
    }
}
