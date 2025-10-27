package com.edstruments.multitenantresourcemanagement.enums;

/**
 * Enumeration of user roles within a tenant.
 * Defines the authorization hierarchy for the system.
 */
public enum UserRole {
    /**
     * ADMIN: Can manage users and resources within their tenant.
     * Has full control over tenant operations.
     */
    ADMIN,

    /**
     * MANAGER: Can manage resources but cannot manage users.
     * Can create, update, and delete resources.
     */
    MANAGER,

    /**
     * EMPLOYEE: Can view resources only.
     * Read-only access to resources within their tenant.
     */
    EMPLOYEE
}

