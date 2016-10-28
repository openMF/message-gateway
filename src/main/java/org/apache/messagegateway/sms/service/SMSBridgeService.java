package org.apache.messagegateway.sms.service;

import java.util.Collection;
import java.util.Date;

import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.apache.messagegateway.sms.repository.SMSBridgeRepository;
import org.apache.messagegateway.sms.serialization.SmsBridgeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "apacheServiceBridge")
public class SMSBridgeService {

	// TODO://Do we need implement ApplicationEventPublisherAware?
	//private ApplicationEventPublisher applicationEventPublisher;

	private final SMSBridgeRepository smsBridgeConfigRepository;

	private final SmsBridgeSerializer smsBridgeService ;
	
	private final SecurityService securityService ;
	
	@Autowired
	public SMSBridgeService(final SMSBridgeRepository smsBridgeConfigRepository,
			final SmsBridgeSerializer smsBridgeService,
			final SecurityService securityService) {
		this.smsBridgeConfigRepository = smsBridgeConfigRepository;
		this.smsBridgeService = smsBridgeService ;
		this.securityService = securityService ;
	}

	/*
	 * @Override public void
	 * setApplicationEventPublisher(ApplicationEventPublisher
	 * applicationEventPublisher) { this.applicationEventPublisher =
	 * applicationEventPublisher; }
	 */

	public Collection<SMSBridge> retrieveProviderDetails(final String tenantId) {
		return this.smsBridgeConfigRepository.findByTenantId(tenantId);
	}

	@Transactional
	public Long createSmsBridgeConfig(final SMSBridge smsBridge) {
		this.smsBridgeService.validateCreate(smsBridge) ;
		smsBridge.setSMSBridgeToBridgeConfigs(); 
		smsBridge.setProviderAppKey(this.securityService.generateApiKey(smsBridge));
		smsBridge.setCreatedDate(new Date());
		final SMSBridge newSMSmsBridge = this.smsBridgeConfigRepository.save(smsBridge);
		return newSMSmsBridge.getId();
	}

	@Transactional
	public void updateSmsBridge(final Long bridgeId, final String json) {
		final SMSBridge bridge = this.smsBridgeConfigRepository.findOne(bridgeId) ;
		this.smsBridgeService.validateUpdate(json, bridge);
		this.smsBridgeConfigRepository.save(bridge);
	}
	
	public SMSBridge retrieveProviderDetails(final String tenantId, final Long providerId)
			throws SMSBridgeNotFoundException {
		final SMSBridge bridge = this.smsBridgeConfigRepository.findByIdAndTenantId(providerId, tenantId);
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(tenantId, providerId);
		}
		return bridge;
	}
}
