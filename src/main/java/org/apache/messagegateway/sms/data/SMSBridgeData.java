package org.apache.messagegateway.sms.data;

import java.io.Serializable;

public class SMSBridgeData implements Serializable {

	private final String tenantId;
	private final String phoneNo;
	private final String providerAppKey;
	private final String providerName;

	public SMSBridgeData(final String tenantId, final String phoneNo, final String appKey, final String providerName) {
		this.tenantId = tenantId;
		this.phoneNo = phoneNo;
		this.providerAppKey = appKey;
		this.providerName = providerName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getProviderAppKey() {
		return providerAppKey;
	}

	public String getProviderName() {
		return providerName;
	}
	
	
	
}
