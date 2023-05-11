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
package org.fineract.messagegateway.sms.providers.impl.jasmin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.fineract.messagegateway.sms.providers.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@Service(value="JasminSMS")
public class JasminSMSProvider extends Provider {

    private static final Logger logger = LoggerFactory.getLogger(JasminSMSProvider.class);

    private HashMap<String, OkHttpClient> restClients = new HashMap<>() ; 
    
    private static final String SCHEME = "http";
    
    private final String callBackUrl ;
    
    @Autowired
		public JasminSMSProvider(final HostConfig hostConfig) {
		callBackUrl = String.format("%s://%s:%d/jasminsms/report/", hostConfig.getProtocol(),  hostConfig.getHostName(), hostConfig.getPort());
    	logger.info("Registering call back to jasminsms:"+callBackUrl);
	}


	@Override
	public void sendMessage(SMSBridge smsBridgeConfig, OutboundMessages message) throws MessageGatewayException {
		logger.info("Reached Jasmin Provider...");
		OkHttpClient okHttpClient = getRestClient(smsBridgeConfig);
		String baseURL = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_URL);
    	String providerAccountId = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_ACCOUNT_ID) ;
    	String providerAuthToken = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_TOKEN) ;
    	String sender = smsBridgeConfig.getConfigValue(MessageGatewayConstants.SENDER_NAME);
    	logger.info("Base URL.....{}", baseURL);
		try {
			URI uri = new URIBuilder()
			        .setScheme(SCHEME)
			        .setHost(baseURL)
			        //.setPath("/send")
			        .setParameter("to", message.getMobileNumber())
			        .setParameter("from", sender)
			        //.setParameter("coding", "")
			        .setParameter("username", providerAccountId)
			        .setParameter("password", providerAuthToken)
			        .setParameter("priority", "2")
			        //.setParameter("sdt", "")  //000000000100000R (send in 1 minute)
			        //.setParameter("validity-period", "")
			        .setParameter("dlr", "yes")
			        .setParameter("dlr-url", callBackUrl)
			        .setParameter("dlr-level", "2")
			        .setParameter("dlr-method", "GET")
			        //.setParameter("tags", "")
			        .setParameter("content", message.getMessage())
			        //.setParameter("hex-content", "")
			        .build();
			
			HttpGet httpget = new HttpGet(uri);
			
			URL url =  httpget.getURI().toURL(); 
			
			Request request = new Request.Builder()
				   .url(url)
				   .build(); 
		
			Response response = okHttpClient.newCall(request).execute();
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
			message.setDeliveredOnDate(new Date());
			message.setResponse(response.body().string());

		} catch (IOException | URISyntaxException e) {
			throw new MessageGatewayException(e.getMessage());
		}

	}
	@Override
	public void sendEmail(SMSBridge smsBridgeConfig, OutboundMessages message){

	}

	@Override
	public void updateStatusByMessageId(SMSBridge bridge, String externalId, String orchestrator) throws MessageGatewayException {

	}

	@Override
	public void publishZeebeVariable(OutboundMessages message) {

	}


	private OkHttpClient getRestClient(final SMSBridge smsBridge) {
    	String authorizationKey = encodeBase64(smsBridge) ;
    	OkHttpClient client = this.restClients.get(authorizationKey) ;
		if(client == null) {
			client = this.get(smsBridge) ;
			this.restClients.put(authorizationKey, client) ;
		}
	    return client ;
    }
    
	OkHttpClient get(final SMSBridge smsBridgeConfig) {
    	logger.debug("Creating a new Twilio Client ....");

        final OkHttpClient client = new OkHttpClient();
        return client;
    }
    
}
