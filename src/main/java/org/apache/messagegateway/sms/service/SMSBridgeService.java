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

	private final SMSBridgeRepository smsBridgeRepository;

	private final SmsBridgeSerializer smsBridgeService ;
	
	private final SecurityService securityService ;
	
	@Autowired
	public SMSBridgeService(final SMSBridgeRepository smsBridgeRepository,
			final SmsBridgeSerializer smsBridgeService,
			final SecurityService securityService) {
		this.smsBridgeRepository = smsBridgeRepository;
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
		return this.smsBridgeRepository.findByTenantId(tenantId);
	}

	@Transactional
	public Long createSmsBridgeConfig(final String json) {
		SMSBridge smsBridge = this.smsBridgeService.validateCreate(json) ;
		smsBridge.setSMSBridgeToBridgeConfigs(); 
		smsBridge.setProviderAppKey(this.securityService.generateApiKey(smsBridge));
		smsBridge.setCreatedDate(new Date());
		final SMSBridge newSMSmsBridge = this.smsBridgeRepository.save(smsBridge);
		return newSMSmsBridge.getId();
	}

	@Transactional
	public void updateSmsBridge(final Long bridgeId, final String json) {
		final SMSBridge bridge = this.smsBridgeRepository.findOne(bridgeId) ;
		this.smsBridgeService.validateUpdate(json, bridge);
		this.smsBridgeRepository.save(bridge);
	}
	
	public Long deleteSmsBridge(final Long bridgeId) throws SMSBridgeNotFoundException{
		final SMSBridge bridge = this.smsBridgeRepository.findOne(bridgeId) ;
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(bridgeId);
		}
		
		this.smsBridgeRepository.delete(bridge);
		return bridgeId ;
	}
	
	public SMSBridge retrieveSmsBridge(final String tenantId, final Long providerId)
			throws SMSBridgeNotFoundException {
		final SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(providerId, tenantId);
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(providerId);
		}
		return bridge;
	}
}
