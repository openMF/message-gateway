package org.apache.messagegateway.sms.repository;

import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSBridgeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSBridgeConfigRepository
		extends JpaRepository<SMSBridgeConfig, Long>, JpaSpecificationExecutor<SMSBridgeConfig> {
	public SMSBridge findByBridge(@Param("bridge") final Long tenantProvider);
}
