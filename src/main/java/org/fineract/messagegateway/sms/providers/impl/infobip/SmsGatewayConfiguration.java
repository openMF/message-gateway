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

import java.util.Collection;

import org.fineract.messagegateway.sms.domain.SMSBridgeConfig;


/** 
 * Class methods each return an SMS gateway configuration property stored in the "configuration" table 
 **/
public class SmsGatewayConfiguration {
	private Boolean developmentMode;
	private String systemId;
	private String hostname;
	private Integer portNumber;
	private String password;
	private Boolean enableOutboundMessageScheduler;
	
	public static final String DEVELOPMENT_MODE = "DEVELOPMENT_MODE";
	public static final String SMS_GATEWAY_SYSTEM_ID = "SMS_GATEWAY_SYSTEM_ID";
	public static final String SMS_GATEWAY_HOSTNAME = "SMS_GATEWAY_HOSTNAME";
	public static final String SMS_GATEWAY_PORT = "SMS_GATEWAY_PORT";
	public static final String SMS_GATEWAY_PASSWORD = "SMS_GATEWAY_PASSWORD";
	public static final String ENABLE_OUTBOUND_MESSSAGE_SCHEDULER = "ENABLE_OUTBOUND_MESSSAGE_SCHEDULER";
	
	/** 
	 * SmsGatewayConfiguration constructor
	 * 
	 * @param configurationData collection of ConfigurationData objects
	 * @return void
	 **/
	public SmsGatewayConfiguration(Collection<SMSBridgeConfig> configurationDataCollection) {
		
	    for (SMSBridgeConfig configurationData : configurationDataCollection) {
		    if (configurationData.getConfigValue() != null) {
    		    switch (configurationData.getConfigName()) {
    		        case DEVELOPMENT_MODE:
    		            this.developmentMode = false ;//Boolean.valueOf(configurationData.getConfigValue());
    		            break;
    		            
    		        case SMS_GATEWAY_SYSTEM_ID:
    		            this.systemId = "MIFOS" ;//configurationData.getConfigValue();
    		            break;
    		            
    		        case SMS_GATEWAY_HOSTNAME:
    		            this.hostname = "smpp3.infobip.com";//configurationData.getConfigValue();
    		            break;
    		            
    		        case SMS_GATEWAY_PORT:
    		            this.portNumber = 8888 ;//Integer.parseInt(configurationData.getConfigValue());
    		            break;
    		            
    		        case SMS_GATEWAY_PASSWORD:
    		            this.password = "A34x8M5q" ;//configurationData.getConfigValue();
    		            break;
    		            
    		        case ENABLE_OUTBOUND_MESSSAGE_SCHEDULER:
    		            this.enableOutboundMessageScheduler = true ;//Boolean.valueOf(configurationData.getConfigValue());
    		            break;
    		    }
		    }
		}
	}
	
	/** 
	 * @return SMS gateway configuration "development mode" property 
	 **/
	public Boolean getDevelopmentMode() {
		return developmentMode;
	}
	
	/** 
	 * @return SMS gateway configuration "system ID" property 
	 **/
	public String getSystemId() {
		return systemId;
	}
	
	/** 
	 * @return SMS gateway configuration "hostname" property 
	 **/
	public String getHostname() {
		return hostname;
	}
	
	/** 
	 * @return SMS gateway configuration "password" property 
	 **/
	public String getPassword() {
		return password;
	}
	
	/** 
	 * @return SMS gateway configuration "port number" property 
	 **/
	public Integer getPortNumber() {
		return portNumber;
	}
	
	/** 
	 * @return SMS gateway configuration "enable outbound message schedule" property 
	 **/
	public Boolean getEnableOutboundMessageScheduler() {
		return enableOutboundMessageScheduler;
	}
}
