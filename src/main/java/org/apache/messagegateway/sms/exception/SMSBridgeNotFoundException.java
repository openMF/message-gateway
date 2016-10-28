package org.apache.messagegateway.sms.exception;

import org.apache.messagegateway.helpers.AbstractPlatformResourceNotFoundException;

public class SMSBridgeNotFoundException extends AbstractPlatformResourceNotFoundException{

	public SMSBridgeNotFoundException(final Long providerId) {
        super("error.msg.smsbrige.id.invalid", "SMSBridge with identifier " + providerId + " does not exist", providerId);
    }
}
