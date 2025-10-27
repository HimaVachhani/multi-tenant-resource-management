package com.edstruments.multitenantresourcemanagement.service;

import com.edstruments.multitenantresourcemanagement.entity.Resource;
import com.edstruments.multitenantresourcemanagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Value("${quota.max-per-user:10}")
    private int maxPerUser;

    @Value("${quota.max-resources:500}")
    private int maxPerTenant;

    @Transactional
    public Resource createResource(Resource resource) {
        long totalForTenant = resourceRepository.countByTenantId(resource.getTenantId());
        if (totalForTenant >= maxPerTenant) {
            throw new RuntimeException("Tenant resource quota exceeded");
        }
        long userCount = resourceRepository.countByOwnerIdAndTenantId(resource.getOwnerId(), resource.getTenantId());
        if (userCount >= maxPerUser) {
            throw new RuntimeException("User resource quota exceeded");
        }
        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource updateResource(Long id, Long tenantId, Resource payload) {
        Resource r = resourceRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        r.setName(payload.getName());
        r.setDescription(payload.getDescription());
        return resourceRepository.save(r);
    }

    @Transactional
    public void deleteResource(Long id, Long tenantId) {
        Resource r = resourceRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        resourceRepository.deleteById(id); // will perform soft-delete via @SQLDelete
    }

    public Page<Resource> listResources(Long tenantId, String name, Long ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (name != null && ownerId != null) {
            return resourceRepository.searchResources(tenantId, name, ownerId, pageable);
        } else if (name != null) {
            return resourceRepository.searchByName(tenantId, name, pageable);
        } else if (ownerId != null) {
            return resourceRepository.findByTenantIdAndOwnerId(tenantId, ownerId, pageable);
        } else {
            return resourceRepository.findByTenantId(tenantId, pageable);
        }
    }
}
