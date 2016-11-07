/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fineract.messagegateway.sms.service;

import java.util.Collection;

import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import org.fineract.messagegateway.sms.serialization.SmsBridgeSerializer;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "apacheServiceBridge")
public class SMSBridgeService {

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

	public Collection<SMSBridge> retrieveProviderDetails(final String tenantId, final String tenantAppKey) {
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		return this.smsBridgeRepository.findByTenantId(tenant.getId());
	}

	@Transactional
	public Long createSmsBridgeConfig(final String tenantId, final String tenantAppKey, final String json) {
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		SMSBridge smsBridge = this.smsBridgeService.validateCreate(json, tenant) ;
		final SMSBridge newSMSmsBridge = this.smsBridgeRepository.save(smsBridge);
		return newSMSmsBridge.getId();
	}

	@Transactional
	public void updateSmsBridge(final String tenantId, final String tenantAppKey, final Long bridgeId, final String json) {
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		final SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId()) ;
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(bridgeId);
		}
		this.smsBridgeService.validateUpdate(json, bridge);
		this.smsBridgeRepository.save(bridge);
	}
	
	public Long deleteSmsBridge(final String tenantId, final String tenantAppKey, final Long bridgeId) throws SMSBridgeNotFoundException{
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		final SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId()) ;
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(bridgeId);
		}
		
		this.smsBridgeRepository.delete(bridge);
		return bridgeId ;
	}
	
	public SMSBridge retrieveSmsBridge(final String tenantId, final String tenantAppKey, final Long bridgeId)
			throws SMSBridgeNotFoundException {
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		final SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId());
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(bridgeId);
		}
		return bridge;
	}
}
