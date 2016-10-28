package org.apache.messagegateway.sms.providers.impl.infobip;

import java.util.Collection;

import org.apache.messagegateway.sms.domain.SMSBridgeConfig;

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
    		            this.developmentMode = Boolean.valueOf(configurationData.getConfigValue());
    		            break;
    		            
    		        case SMS_GATEWAY_SYSTEM_ID:
    		            this.systemId = configurationData.getConfigValue();
    		            break;
    		            
    		        case SMS_GATEWAY_HOSTNAME:
    		            this.hostname = configurationData.getConfigValue();
    		            break;
    		            
    		        case SMS_GATEWAY_PORT:
    		            this.portNumber = Integer.parseInt(configurationData.getConfigValue());
    		            break;
    		            
    		        case SMS_GATEWAY_PASSWORD:
    		            this.password = configurationData.getConfigValue();
    		            break;
    		            
    		        case ENABLE_OUTBOUND_MESSSAGE_SCHEDULER:
    		            this.enableOutboundMessageScheduler = Boolean.valueOf(configurationData.getConfigValue());
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
