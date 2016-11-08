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
package org.fineract.messagegateway.sms.providers.impl.infobip;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;

import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.providers.impl.twilio.TwilioMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import infobip.api.client.SendMultipleTextualSmsAdvanced;
import infobip.api.config.BasicAuthConfiguration;
import infobip.api.model.Destination;
import infobip.api.model.sms.mt.send.Message;
import infobip.api.model.sms.mt.send.SMSResponse;
import infobip.api.model.sms.mt.send.SMSResponseDetails;
import infobip.api.model.sms.mt.send.textual.SMSAdvancedTextualRequest;

@Service(value = "InfoBip")
public class InfoBipMessageProvider implements SMSProvider {

	private static final Logger logger = LoggerFactory.getLogger(InfoBipMessageProvider.class);

	private HashMap<String, SendMultipleTextualSmsAdvanced> restClients = new HashMap<>() ; //tenantId, twilio clients
	private final String callBackUrl ;
	 
	private final StringBuilder builder ;
	  
	@Autowired
	public InfoBipMessageProvider(final HostConfig hostConfig) {
		callBackUrl = String.format("%s://%s:%d/infobip/report/", hostConfig.getProtocol(),  hostConfig.getHostName(), hostConfig.getPort());
    	logger.info("Registering call back to twilio:"+callBackUrl);
    	builder = new StringBuilder() ;
	}

	@Override
	public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message) throws MessageGatewayException {
		SendMultipleTextualSmsAdvanced client = getRestClient(smsBridgeConfig) ;
		Destination destination = new Destination();
		builder.setLength(0);
        builder.append(smsBridgeConfig.getCountryCode()) ;
        builder.append(message.getMobileNumber()) ;
        String mobile = builder.toString() ;
		destination.setTo(mobile);
		Message infoBipMessage = new Message();
		infoBipMessage.setDestinations(Collections.singletonList(destination));
		infoBipMessage.setText(message.getMessage());
		infoBipMessage.setNotifyUrl(callBackUrl);
		SMSAdvancedTextualRequest requestBody = new SMSAdvancedTextualRequest();
		requestBody.setMessages(Collections.singletonList(infoBipMessage));
		SMSResponse response = client.execute(requestBody);
		SMSResponseDetails sentMessageInfo = response.getMessages().get(0);
		message.setExternalId(sentMessageInfo.getMessageId());
		//message.setDeliveryStatus(sentMessageInfo.getStatus().);
	}
	
	
	private SendMultipleTextualSmsAdvanced getRestClient(final SMSBridge smsBridge) {
		String authorizationKey = encodeBase64(smsBridge) ;
		 SendMultipleTextualSmsAdvanced client = this.restClients.get(authorizationKey) ;
		if(client == null) {
			client = this.get(smsBridge) ;
			this.restClients.put(authorizationKey, client) ;
		}
	    	return client ;
	 }
	 
	SendMultipleTextualSmsAdvanced get(final SMSBridge smsBridgeConfig) {
    	logger.debug("Creating a new InfoBip Client ....");
    	String userName = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_ACCOUNT_ID) ;
    	String password = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_AUTH_TOKEN) ;
    	final SendMultipleTextualSmsAdvanced client = new SendMultipleTextualSmsAdvanced(new BasicAuthConfiguration(userName, password));
        return client;
    }
	
	private String encodeBase64(final SMSBridge smsBridgeConfig) {
		String tenant = smsBridgeConfig.getTenantId().toString() ;
		String username = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_ACCOUNT_ID) ;
    	String password = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_AUTH_TOKEN) ;
        String userPass = username + ":" + password + ":" + tenant;
        return Base64.getEncoder().encodeToString(userPass.getBytes());
    }
}
