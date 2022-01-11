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
package org.fineract.messagegateway.sms.providers.impl.twilio;

import java.util.Date;
import java.util.HashMap;

import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

@Service(value="Twilio")
public class TwilioMessageProvider extends SMSProvider {

    private static final Logger logger = LoggerFactory.getLogger(TwilioMessageProvider.class);

    private HashMap<String, TwilioRestClient> restClients = new HashMap<>() ; //tenantId, twilio clients
    
    
    private final String callBackUrl ;
    
    private final StringBuilder builder ;
    
    @Autowired
    TwilioMessageProvider(final HostConfig hostConfig) {
    	builder = new StringBuilder() ;
    	callBackUrl = String.format("%s://%s:%d/twilio/report/", hostConfig.getProtocol(),  hostConfig.getHostName(), hostConfig.getPort());
    	logger.info("Registering call back to twilio:"+callBackUrl);
    }

    
    @Override
    public void sendMessage(final SMSBridge smsBridgeConfig, final SMSMessage message)
        throws MessageGatewayException {
    	//Based on message id, register call back. so that we get notification from Twilio about message status
    	String statusCallback = callBackUrl+message.getId() ;
        final TwilioRestClient twilioRestClient = this.getRestClient(smsBridgeConfig);
        builder.setLength(0);
        builder.append(smsBridgeConfig.getCountryCode()) ;
        builder.append(message.getMobileNumber()) ;
        String mobile = builder.toString() ;
        logger.info("Sending SMS to " + mobile + " ...");
        MessageCreator creator = new MessageCreator(new PhoneNumber(mobile), new PhoneNumber(smsBridgeConfig.getPhoneNo()) , message.getMessage() ) ;
        creator.setStatusCallback(statusCallback) ;
        try {
        	message.setSubmittedOnDate(new Date());
        	Message twilioMessage = creator.create(twilioRestClient) ;
        	message.setExternalId(twilioMessage.getSid());
        	logger.debug("TwilioMessageProvider.sendMessage():"+TwilioStatus.smsStatus(twilioMessage.getStatus()).getValue());
        	message.setDeliveryStatus(TwilioStatus.smsStatus(twilioMessage.getStatus()).getValue()) ;
        	if(message.getDeliveryStatus().equals(SmsMessageStatusType.FAILED.getValue())) {
        		message.setDeliveryErrorMessage(twilioMessage.getErrorMessage());
        		logger.error("Sending SMS to :"+message.getMobileNumber()+" failed with reason "+twilioMessage.getErrorMessage());
        	}
        }catch (ApiException e) {
        	logger.error("ApiException while sending message to :"+message.getMobileNumber()+" with reason "+e.getMessage());
        	message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
        	message.setDeliveryErrorMessage(e.getMessage());
        }
    }

    @Override
    public void updateStatusByMessageId(SMSBridge bridge, String externalId) throws MessageGatewayException {

    }

    private TwilioRestClient getRestClient(final SMSBridge smsBridge) {
    	String authorizationKey = encodeBase64(smsBridge) ;
    	TwilioRestClient client = this.restClients.get(authorizationKey) ;
		if(client == null) {
			client = this.get(smsBridge) ;
			this.restClients.put(authorizationKey, client) ;
		}
	    return client ;
    }
    
    TwilioRestClient get(final SMSBridge smsBridgeConfig) {
    	logger.debug("Creating a new Twilio Client ....");
    	String providerAccountId = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_ACCOUNT_ID) ;
    	String providerAuthToken = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_TOKEN) ;
        final TwilioRestClient client = new TwilioRestClient.Builder(providerAccountId, providerAuthToken).build();
        return client;
    }
}
