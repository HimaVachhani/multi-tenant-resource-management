package com.edstruments.multitenantresourcemanagement.enums;

/**
 * Enumeration of actions that can be audited in the system.
 * Used for comprehensive audit logging.
 */
public enum AuditAction {
    // User actions
    CREATED_USER,
    UPDATED_USER,
    DELETED_USER,
    
    // Resource actions
    CREATED_RESOURCE,
    UPDATED_RESOURCE,
    DELETED_RESOURCE,
    
    // Tenant actions
    CREATED_TENANT,
    DELETED_TENANT,
    
    // Authentication actions
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT
}

