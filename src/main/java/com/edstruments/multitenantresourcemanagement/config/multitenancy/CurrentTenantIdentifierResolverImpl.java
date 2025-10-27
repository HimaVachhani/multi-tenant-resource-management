package com.edstruments.multitenantresourcemanagement.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        return (tenant != null) ? tenant : "public"; // default schema
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
