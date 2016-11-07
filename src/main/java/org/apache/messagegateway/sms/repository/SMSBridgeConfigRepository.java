package org.apache.messagegateway.sms.repository;

import org.apache.messagegateway.sms.domain.SMSBridgeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSBridgeConfigRepository
		extends JpaRepository<SMSBridgeConfig, Long>, JpaSpecificationExecutor<SMSBridgeConfig> {
}
