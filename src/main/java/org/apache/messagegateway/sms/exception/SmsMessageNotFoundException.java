package org.apache.messagegateway.sms.exception;

@SuppressWarnings("serial")
public class SmsMessageNotFoundException extends RuntimeException {
	
    /** 
	 * SmsMessageNotFoundException constructor
	 * @param message the exception message
	 **/
	public SmsMessageNotFoundException(String message) {
		super(message);
	}
}
