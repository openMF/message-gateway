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
package org.fineract.messagegateway.sms.data;

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
