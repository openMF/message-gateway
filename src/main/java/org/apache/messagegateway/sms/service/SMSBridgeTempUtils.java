package org.apache.messagegateway.sms.service;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.messagegateway.sms.domain.SMSBridge;

public class SMSBridgeTempUtils {

	private final static SMSBridgeTempUtils INSTANCE = new SMSBridgeTempUtils() ;
	
	private Collection<SMSBridge> temp ;
	
	private SMSBridgeTempUtils() {
		this.temp = new ArrayList<>() ;
		this.temp.add(new SMSBridge(1L,"default","8861308914", "default_twilio", "Twilio SMS Provider", "")) ;
		this.temp.add(new SMSBridge(2L, "default1","8861308914", "default_twilio", "Twilio SMS Provider1", "")) ;
		this.temp.add(new SMSBridge(3L, "default2","8861308914", "default_twilio", "Twilio SMS Provider1", "")) ;
	}
	
	public static SMSBridgeTempUtils getInstance() {
		return INSTANCE ;
	}
	
	public Collection<SMSBridge> getSMSProviders(final String tenantId) {
		return this.temp ;
	}
	
}
