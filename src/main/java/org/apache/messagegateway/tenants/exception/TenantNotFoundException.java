package org.apache.messagegateway.tenants.exception;

public class TenantNotFoundException extends RuntimeException{

	public TenantNotFoundException(final String tenantId, final String tenantAppKey) {
		super("Tenant with identifier "+tenantId + "not found") ;
	}
}
