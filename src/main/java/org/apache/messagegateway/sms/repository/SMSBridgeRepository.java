package org.apache.messagegateway.sms.repository;

import java.util.Collection;

import org.apache.messagegateway.sms.domain.SMSBridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSBridgeRepository extends JpaRepository<SMSBridge, Long>, JpaSpecificationExecutor<SMSBridge> {

	
	public Collection<SMSBridge> findByTenantId(@Param("tenantId") final String tenantId) ;
	
	public SMSBridge findByProviderAppKey(@Param("providerAppKey") final String providerAppKey) ;
	
	public SMSBridge findByIdAndTenantId(@Param("id") final Long id, @Param("tenantId") final String tenantId) ; 
}
