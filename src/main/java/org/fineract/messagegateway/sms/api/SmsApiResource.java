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
package org.fineract.messagegateway.sms.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.exception.ProviderNotDefinedException;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.providers.impl.infobip.InfoBipApiResource;
import org.fineract.messagegateway.sms.providers.impl.telerivet.TelerivetMessageProvider;
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.service.SMSMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsApiResource {

	//This class sends TRANSACTIONAL & PROMOTIONAL SMS

	private SMSMessageService smsMessageService ;

	private static final Logger logger = LoggerFactory.getLogger(SmsApiResource.class);

	@Autowired
	private TelerivetMessageProvider telerivetMessageProvider;

	@Autowired
	private  SMSBridgeRepository smsBridgeRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
    public SmsApiResource(final SMSMessageService smsMessageService) {
		this.smsMessageService = smsMessageService ;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> sendShortMessages(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    		@RequestBody final List<SMSMessage> payload) {
		logger.info("Payload "+ payload.get(0).getMessage());
    	this.smsMessageService.sendShortMessage(tenantId, appKey, payload);
       return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<DeliveryStatusData>> getDeliveryStatus(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    		@RequestBody final Collection<Long> internalIds) throws MessageGatewayException {
    	Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryStatus(tenantId, appKey, internalIds) ;
		for(DeliveryStatusData deliveryStatusData :  deliveryStatus) {
			if (deliveryStatusData.getDeliveryStatus() != 300) {
				logger.info("Delivery status is still pending, fetching message status manually ");
				SMSBridge bridge = smsBridgeRepository.findByIdAndTenantId(deliveryStatusData.getBridgeId(),
						deliveryStatusData.getTenantId());
				SMSProvider provider = null;
				try {
					if (bridge == null) {
						throw new SMSBridgeNotFoundException(deliveryStatusData.getBridgeId());
					}
					logger.info("Finding provider for fetching message status....{}", bridge.getProviderKey());
					provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey());
					if (provider == null)
						throw new ProviderNotDefinedException();
					provider.updateStatusByMessageId(bridge, deliveryStatusData.getExternalId());
					Collection<Long> id = new ArrayList<Long>();
					id.add(Long.valueOf(deliveryStatusData.getId()));
					Collection<DeliveryStatusData> messageDeliveryStatus = this.smsMessageService.getDeliveryStatus(tenantId, appKey, id);
					deliveryStatus = messageDeliveryStatus;
				} catch (ProviderNotDefinedException e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);

	}
}
