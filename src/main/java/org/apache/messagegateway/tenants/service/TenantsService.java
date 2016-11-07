package org.apache.messagegateway.tenants.service;

import org.apache.messagegateway.service.SecurityService;
import org.apache.messagegateway.tenants.domain.Tenant;
import org.apache.messagegateway.tenants.exception.TenantNotFoundException;
import org.apache.messagegateway.tenants.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantsService {

	private final TenantRepository tenantRepository ;
	
	private final SecurityService securityService ;
	
	@Autowired
	public TenantsService(final TenantRepository tenantRepository,
			final SecurityService securityService) {
		this.tenantRepository = tenantRepository ;
		this.securityService = securityService ;
	}
	
	public String createTenant(final Tenant tenant) {
		tenant.setTenantAppKey(this.securityService.generateApiKey(tenant.getTenantId()));
		this.tenantRepository.save(tenant) ;
		return tenant.getTenantAppKey() ;
	}
	
	public Tenant findTenantByTenantIdAndTenantAppKey(final String tenantId, final String tenantAppKey) {
		Tenant tenant = this.tenantRepository.findByTenantIdAndTenantAppKey(tenantId, tenantAppKey) ;
		if(tenant == null) {
			throw new TenantNotFoundException(tenantId, tenantAppKey) ;
		}
		return tenant ;
	}
	
	public Tenant findTenantByTenantId(final String tenantId) {
		Tenant tenant = this.tenantRepository.findByTenantId(tenantId) ;
		if(tenant == null) {
			throw new TenantNotFoundException(tenantId, "") ;
		}
		return tenant ;
	}
}
