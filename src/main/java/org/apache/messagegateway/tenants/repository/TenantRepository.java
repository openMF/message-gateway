package org.apache.messagegateway.tenants.repository;

import org.apache.messagegateway.tenants.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository
		extends JpaRepository<Tenant, Long>, JpaSpecificationExecutor<Tenant> {
	
	Tenant findByTenantId(@Param("tenantId") final String tenantId) ;
	
	Tenant findByTenantIdAndTenantAppKey(@Param("tenantId") final String tenantId, @Param("tenantAppKey") final String tenantAppKey) ;
}
