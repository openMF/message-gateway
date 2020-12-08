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
package org.fineract.messagegateway.sms.providers;

import java.util.Collection;

import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.exception.ProviderNotDefinedException;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SMSProviderFactory implements ApplicationContextAware {

	 private static final Logger logger = LoggerFactory.getLogger(SMSProviderFactory.class);
	 
	private ApplicationContext applicationContext;

	private final SMSBridgeRepository smsBridgeRepository;

	@Autowired
	public SMSProviderFactory(final SMSBridgeRepository smsBridgeRepository) {
		this.smsBridgeRepository = smsBridgeRepository;
	}

	public SMSProvider getSMSProvider(final SMSMessage message) throws SMSBridgeNotFoundException, ProviderNotDefinedException {
		SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
				message.getTenantId());
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(message.getBridgeId());
		}
		SMSProvider provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
		if(provider == null) throw new ProviderNotDefinedException() ;
		return provider ;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void sendShortMessage(final SMSMessage message) {
		SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
				message.getTenantId());
		SMSProvider provider = null;
		try {
			if (bridge == null) {
				throw new SMSBridgeNotFoundException(message.getBridgeId());
			}
			provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
			if (provider == null) throw new ProviderNotDefinedException();
			provider.sendMessage(bridge, message);
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
		} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException | BeansException e) {
			logger.error(e.getMessage());
			message.setDeliveryErrorMessage(e.getMessage());
			message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
		}
	}
	
	public void sendShortMessage(final Collection<SMSMessage> messages) {
		for(SMSMessage message: messages) {
			logger.info("Sending message....{}", message.getId());
			SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
					message.getTenantId());
			SMSProvider provider = null;
			try {
				if (bridge == null) {
					throw new SMSBridgeNotFoundException(message.getBridgeId());
				}
				logger.info("Finding provider....{}", bridge.getProviderKey());
				provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
				if (provider == null)
					throw new ProviderNotDefinedException();
				provider.sendMessage(bridge, message);
			} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException | BeansException e) {
				logger.error(e.getMessage());
				message.setDeliveryErrorMessage(e.getMessage());
				message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
			}
		}
	}
}
