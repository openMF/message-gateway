package org.apache.messagegateway.sms.providers;

import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;

public interface SMSProvider {
	
	public void sendMessage(final SMSBridge smsBridgeConfig, final SMSMessage message)
	        throws MessageGatewayException ;
}
