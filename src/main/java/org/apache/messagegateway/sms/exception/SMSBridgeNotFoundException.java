package org.apache.messagegateway.sms.exception;

public class SMSBridgeNotFoundException extends Exception{

	public SMSBridgeNotFoundException(final String tenantId, final Long providerId) {
		super("SMSBridge with tenantId:"+tenantId+" and providerId:"+providerId+" Does not exists") ;
	}
}
